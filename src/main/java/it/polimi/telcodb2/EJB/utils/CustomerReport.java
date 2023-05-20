package it.polimi.telcodb2.EJB.utils;

import it.polimi.telcodb2.EJB.entities.Customer;
import it.polimi.telcodb2.EJB.entities.InsolventCustomersReportView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;


public class CustomerReport {

    private static class AlertReport {
        private Integer idAlert;
        private LocalDateTime lastPayment;
        private BigDecimal amount;

        public AlertReport(Integer idAlert, LocalDateTime lastPayment, BigDecimal amount) {
            this.idAlert = idAlert;
            this.lastPayment = lastPayment;
            this.amount = amount;
        }

        public Integer getIdAlert() {
            return idAlert;
        }

        public void setIdAlert(Integer idAlert) {
            this.idAlert = idAlert;
        }

        public LocalDateTime getLastPayment() {
            return lastPayment;
        }

        public void setLastPayment(LocalDateTime lastPayment) {
            this.lastPayment = lastPayment;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    private static class OrderReport {
        private Integer idOrder;
        private LocalDate startDate;
        private LocalDateTime creationDateTime;
        private BigDecimal totalCost;
        private Integer idPackage;
        private Integer idValidity;

        public OrderReport(Integer idOrder, LocalDate startDate, LocalDateTime creationDateTime, BigDecimal totalCost, Integer idPackage, Integer idValidity) {
            this.idOrder = idOrder;
            this.startDate = startDate;
            this.creationDateTime = creationDateTime;
            this.totalCost = totalCost;
            this.idPackage = idPackage;
            this.idValidity = idValidity;
        }

        public Integer getIdOrder() {
            return idOrder;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDateTime getCreationDateTime() {
            return creationDateTime;
        }

        public BigDecimal getTotalCost() {
            return totalCost;
        }

        public Integer getIdPackage() {
            return idPackage;
        }

        public Integer getIdValidity() {
            return idValidity;
        }
    }

    private Integer idCustomer;
    private String username;
    private String email;

    private List<OrderReport> orderReportList;
    private AlertReport alertReport;

    public CustomerReport(Integer idCustomer, String username, String email, Integer idAlert, LocalDateTime lastPayment, BigDecimal amount) {
        this.idCustomer = idCustomer;
        this.username = username;
        this.email = email;
        this.orderReportList = new ArrayList<OrderReport>();
        this.alertReport = new AlertReport(idAlert, lastPayment, amount);
    }

    public void setOrderReportList(List<OrderReport> orderReportList) {
        this.orderReportList = orderReportList;
    }

    public Integer getIdCustomer() {
        return idCustomer;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<OrderReport> getOrderReportList() {
        return orderReportList;
    }

    public AlertReport getAlertReport() {
        return alertReport;
    }

    public boolean hasAlert() {
        boolean result = this.alertReport.getIdAlert() != null && this.alertReport.getAmount() != null && this.alertReport.getLastPayment() != null;
        return result;
    }

    public static List<CustomerReport> fromCustomersReportView(List<InsolventCustomersReportView> insolventCustomersReportViewList) {
        // Partition the view list grouping by customer id
        Map<Integer, List<InsolventCustomersReportView>> partitionedList = insolventCustomersReportViewList.stream()
                .collect(groupingBy(InsolventCustomersReportView::getIdCustomer));


        // For each list in the partition map create a list of customer reports
        List<CustomerReport> customerReportList = new ArrayList<CustomerReport>();
        for (Integer id : partitionedList.keySet()) {
            List<InsolventCustomersReportView> reportViewList = partitionedList.get(id);
            CustomerReport customerReport = new CustomerReport(
                    reportViewList.get(0).getIdCustomer(),
                    reportViewList.get(0).getUsername(),
                    reportViewList.get(0).getEmail(),
                    reportViewList.get(0).getIdAlert(),
                    reportViewList.get(0).getLastPayment(),
                    reportViewList.get(0).getAmount()
            );
            customerReport.setOrderReportList(
                    reportViewList.stream()
                            .map(reportView -> new OrderReport(
                            reportView.getIdOrder(),
                            reportView.getStartDate(),
                            reportView.getCreationDateTime(),
                            reportView.getTotalCost(),
                            reportView.getIdPackage(),
                            reportView.getIdValidity())
                            )
                            .collect(Collectors.toList())
            );
            customerReportList.add(customerReport);
        }

        return customerReportList;
    }
}
