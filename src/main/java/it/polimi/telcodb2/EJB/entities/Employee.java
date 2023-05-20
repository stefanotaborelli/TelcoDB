package it.polimi.telcodb2.EJB.entities;


import javax.persistence.*;

@Entity
@Table(name = "Employee", schema = "TelcoDB")
@NamedQueries({
        @NamedQuery(
                name = "Employee.checkCredentials",
                query = "SELECT e FROM Employee e  WHERE e.username = :username and e.password = :password" )
})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEmployee", nullable = false)
    private int idEmployee;

    @Column(name="username", nullable = false)
    private String username;

    @Column(name="password", nullable = false)
    private String password;

    public Employee() {
    }

    public Employee(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}