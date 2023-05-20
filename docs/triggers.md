# Materialized views

The following materialized views have been designed:
1. `TotalSalesValue_mv`: total value of sales per package with and without the optional products;
2. `PurchasesPerPackage_mv`: number of total purchases per package;
3. `PurchasesPerPackageValidity_mv`: number of total purchases per package and validity period;
4. `BestSellerProducts_mv`: bestseller optional product, i.e. the optional product with the greatest value of sales 
across all the sold service packages.Number of total purchases per package;
5. `AvgProductsSold_mv`: average number of optional products sold together with each service package;
6. `InsolventCustomersReport_mv`: list of insolvent users, suspended orders and alerts;

## Triggers

### Alert

#### After insert
````
create trigger update_alert_mvs
    after insert
    on Alert
    for each row
BEGIN
    CALL updateInsolventCustomersReport(NEW.idCustomer);
END;
````

The trigger adds in every tuple in `InsolventCustomersReport_mv` with the new values of the alert.

#### After update

````
create trigger update_alert_mvs_2
    after update
    on Alert
    for each row
BEGIN
    CALL updateInsolventCustomersReport(NEW.idCustomer);
END;
````

The trigger updates every tuple in `InsolventCustomersReport_mv` with the new values of the alert.

### Compatible validity

#### After insert
````
create trigger update_chosen_validity_mvs
    after insert
    on CompatibleValidities
    for each row
BEGIN
    SET @name = (SELECT name FROM Package P WHERE P.idPackage = NEW.idPackage);
    SET @duration = 0;
    SET @fee = 0.00;
    SELECT duration, fee
    INTO @duration, @fee
    FROM Validity V
    WHERE V.idValidity = NEW.idValidity;

    INSERT INTO PurchasesPerPackageValidity_mv (hashId, idPackage, name, idValidity, duration, fee, purchases)
    VALUES (MD5(CONCAT(NEW.idPackage, NEW.idValidity)), NEW.idPackage, @name, NEW.idValidity, @duration, @fee, 0);
END;
````

When a package is created, and it is associated with a validity  the trigger creates a new row into 
``PurchasesPerPackageValidity`` with default value ``purchases = 0``.

### Order

#### After insert
````
create trigger update_order_mvs
    after insert
    on `Order`
    for each row
BEGIN
    IF (NEW.paid = true) THEN
        /* Update TotalSalesValue */
        CALL updateTotalSalesValue(NEW.idPackage, NEW.idValidity, NEW.totalCost);
        /* Update PurchasesPerPackage */
        CALL updatePurchasesPerPackage(NEW.idPackage);
        /* Update PurchasesPerPackageValidity */
        CALL updatePurchasesPerPackageValidity(NEW.idPackage, NEW.idValidity);
    ELSE
        /* InsolventCustomersReport */
        CALL updateInsolventCustomersReport(NEW.idCustomer);
    END IF;
END;

````

When an order is created: 
* if it has already been paid, then the trigger updates the statistics associated with the included package;
* if the order has not been paid yet, then the trigger inserts a row into `InsolventCustomersReport_mv` for the order and updates the 
attributes from `Alert` - if there is one - for each row corresponding with the customer.

#### After update

````
create trigger update_order_mvs_2
    after update
    on `Order`
    for each row
BEGIN
    IF (OLD.paid = false AND NEW.paid = true) THEN
        /* Update TotalSalesValue_mv */
        CALL updateTotalSalesValue(NEW.idPackage, NEW.idValidity, NEW.totalCost);
        /* Update AvgProductsSold_mv */
        CALL updateAvgProductsSold(NEW.idPackage);
        /* Update PurchasesPerPackage_mv */
        CALL updatePurchasesPerPackage(NEW.idPackage);
        /* Update PurchasesPerPackageValidity_mv */
        CALL updatePurchasesPerPackageValidity(NEW.idPackage, NEW.idValidity);
        /* BestSellerProduct_mv */
        CALL updateBestSellerProduct(NEW.idOrder);
        
        /* InsolventCustomersReport_mv */
        DELETE FROM InsolventCustomersReport_mv WHERE idOrder = NEW.idOrder;
    END IF;
END;
````

When an order is update from not-paid (`paid=false`) to paid (`paid=true`), then the trigger updates the statistics for the 
included package and delete the corresponding tuple from `InsolventCustomersReport_mv`.

### Package

#### After insert
````
create trigger update_package_mvs
after insert
on Package
for each row
BEGIN
    /* Create default rows in materialized views */
    INSERT INTO AvgProductsSold_mv (idPackage, name, avgNumOfProducts)
    VALUES (NEW.idPackage, NEW.name, 0);

    INSERT INTO PurchasesPerPackage_mv (idPackage, name, purchases)
    VALUES (NEW.idPackage, NEW.name, 0);

    INSERT INTO TotalSalesValue_mv (idPackage, name, completeValue, partialValue)
    VALUES (NEW.idPackage, NEW.name, 0, 0);
END;
````

When a package is created then the trigger adds rows for it in `AvgProductsSold_mv`, `PurchasesPerPackage_mv` and `TotalSalesValue_mv`
with default statistics.

### Product

#### After insert

````
create trigger update_product_mvs
    after insert
    on Product
    for each row
BEGIN
    IF ((SELECT COUNT(*) FROM BestSellerProduct_mv) = 0) THEN
        INSERT INTO BestSellerProduct_mv (idProduct, name, valueOfSales)
        VALUES (NEW.idProduct, NEW.name, 0);
    end if;
end;
````

When a product is created, if it is the first one, then the trigger adds a default tuple for it in ``BestSellerProduct_mv``.

### ChosenProduct

#### After insert

````
create trigger update_chosen_product_mv
    after insert
    on ChosenProduct
    for each row
begin
    set @idPackage = (select O.idPackage from `Order` O where O.idOrder = NEW.idOrder and O.paid = true);
    
    if ((select O.paid from `Order` O where O.idOrder = NEW.idOrder) = true) then
        call updateAvgProductsSold(@idPackage);
        call updateBestSellerProduct(NEW.idOrder);
    end if;
end;
````

When an association between a paid order and its chosen product is created, then the trigger updates the statistics that
involve products.

## Stored procedures

### updateAvgProductsSold
````
create procedure updateAvgProductsSold(IN idPkg int)
BEGIN
    /* Compute average */
    SET @average = 
    (SELECT AVG(DT1.numOfProducts)
    FROM
        (SELECT O.idPackage, COUNT(*) AS numOfProducts
        FROM ChosenProduct CP
            JOIN `Order` O ON CP.idOrder = O.idOrder
        WHERE O.idPackage = idPkg
        GROUP BY O.idOrder)
    AS DT1);

    /* Update with new average */
    UPDATE AvgProductsSold_mv
    SET
        avgNumOfProducts = @average
    WHERE idPackage = idPkg;
END;
````

Given the order id, the procedure computes the average number of product sold with a service package and updates its value inside 
`AvgProductsSold_mv`.

### updateBestSellerProduct
````
create
    definer = admin@localhost procedure updateBestSellerProduct(IN idOrd int)
BEGIN
    /* Get current bestseller product total value */
    SET @currTotalValue = 0.00;
    SET @currId = 0;
    SELECT idProduct, valueOfSales
    INTO @currId, @currTotalValue
    FROM BestSellerProduct_mv
    ORDER BY valueOfSales DESC
    LIMIT 1;

    /* Compute total value of sales of the new product */
    SET @idProd = 0;
    SET @newTotalValue = 0.00;
    SELECT P.idProduct, SUM(P.fee * V.duration) AS totalValue
    INTO @idProd, @newTotalValue
    FROM
        (SELECT idProduct FROM ChosenProduct CP WHERE idOrder = idOrd) AS DT1 
        JOIN ChosenProduct CP ON DT1.idProduct = CP.idProduct
        JOIN `Order` O ON O.idOrder = CP.idOrder
        JOIN Validity V on O.idValidity = V.idValidity
        JOIN Product P on CP.idProduct = P.idProduct
    WHERE O.paid = true
    GROUP BY P.idProduct
    ORDER BY totalValue DESC
    LIMIT 1;

    SET @newName = (SELECT name FROM Product WHERE idProduct = @idProd);

    /* Update the best seller product */
    IF (@currTotalValue < @newTotalValue) THEN
        IF (@currId = @idProd) THEN
            UPDATE BestSellerProduct_mv
            SET valueOfSales = @newTotalValue
            WHERE idProduct = @idProd;
        ELSE
            INSERT INTO BestSellerProduct_mv(idProduct, name, valueOfSales)
            VALUES (@idProd, @newName, @newTotalValue);

            DELETE FROM BestSellerProduct_mv WHERE idProduct != @idProd;
        END IF;
    END IF;
END;
````

Given the order id, the procedure finds the total sales value of each one of the products included in it. Then, if the highest total 
sales value is greater than the one of the current bestseller product, it sets this as the new bestseller product.

### updateInsolventCustomersReport
````
create procedure updateInsolventCustomersReport(IN idCus int)
BEGIN
    INSERT INTO InsolventCustomersReport_mv (idCustomer, username, email, idOrder, startDate, creationDateTime, totalCost, idPackage, idValidity, idAlert, lastPayment, amount)
    SELECT *
    FROM
        (SELECT
            C.idCustomer,
            C.username,
            C.email,
            O.idOrder,
            O.startDate,
            O.creationDateTime,
            O.totalCost,
            O.idPackage,
            O.idValidity,
            A.idAlert,
            A.lastPayment,
            A.amount
        FROM
            Customer C 
            JOIN `Order` O ON C.idCustomer = O.idCustomer
            LEFT JOIN Alert A ON A.idCustomer = C.idCustomer
        WHERE
            O.paid = false AND C.idCustomer = idCus)
    AS DT1
    ON DUPLICATE KEY UPDATE
        idAlert = DT1.idAlert,
        amount = DT1.amount,
        lastPayment = DT1.lastPayment;
END;
````

Given a customer id, the procedure creates a report into `InsolventCustomersReport_mv` for each unpaid order of the customer with:
* the customer data
* the order data
* the alert data if there is one
If there is already a tuple for the order then it updates the attributes for the alert with the current values.

### updatePurchasesPerPackage
````
create procedure updatePurchasesPerPackage(IN idPkg int)
BEGIN
    /* Get current purchases */
    SET @purchases = 0;
    SELECT purchases
    INTO @purchases
    FROM PurchasesPerPackage_mv
    WHERE idPackage = idPkg;

    /* Update with the incremented number of purchases */
    UPDATE PurchasesPerPackage_mv
    SET purchases = @purchases + 1
    WHERE idPackage = idPkg;
END;
````

Given the package id, the procedure increments the count of purchases for the package.

### updatePurchasesPerPackageValidity
````
create procedure updatePurchasesPerPackageValidity(IN idPkg int, IN idVal int)
BEGIN
    /* Get current purchases */
    SET @purchases = 0;
    SELECT purchases
    INTO @purchases
    FROM PurchasesPerPackageValidity_mv
    WHERE hashId = MD5(CONCAT(idPkg, idVal));

    /* Update with the incremented number of purchases */
    UPDATE PurchasesPerPackageValidity_mv
    SET
        purchases = @purchases + 1
    WHERE idPackage = idPkg AND idValidity = idVal;
END;
````

Given the package id and the validity id, the procedure increments the count of purchases for the couple package-validity.

### updateTotalSalesValue
````
create procedure updateTotalSalesValue(IN idPkg int, IN idVal int, IN totVal int)
BEGIN
    /* Get old values */
    SET @completeValue = 0.00;
    SET @partialValue = 0.00;
    SELECT completeValue, partialValue
    INTO @completeValue, @partialValue
    FROM TotalSalesValue_mv
    WHERE idPackage = idPkg;

    /* Get deltas */
    SET @deltaCompleteValue = totVal;
    SET @deltaPartialValue = 0.00;
    SELECT duration * fee
    INTO @deltaPartialValue
    FROM Validity
    WHERE idValidity = idVal;

    /* Update with new values */
    UPDATE TotalSalesValue_mv
    SET
        completeValue = @completeValue + @deltaCompleteValue,
        partialValue = @partialValue + @deltaPartialValue
    WHERE idPackage = idPkg;
END;
````

Given the package id, the validity id and the total value of an order, the procedure updates the total sales value for 
the package.