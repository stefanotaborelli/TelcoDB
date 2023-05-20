package it.polimi.telcodb2.EJB.entities;
import it.polimi.telcodb2.EJB.enums.ServiceType;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Service", schema = "TelcoDB", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"serviceType", "minutes", "extraMinutesFee",
                "sms", "extraSmsFee", "giga", "extraGigaFee"})
})
@NamedQueries(
        {
                @NamedQuery(
                        name = "Service.findEquivalent",
                        query = "SELECT s FROM Service s WHERE s.serviceType = :serviceType " +
                                "AND s.minutes = :minutes AND s.extraMinutesFee = :extraMinutesFee " +
                                "AND s.sms = :sms AND s.extraSmsFee = :extraSmsFee " +
                                "AND s.giga = :giga AND s.extraGigaFee = :extraGigaFee"
                ),
                @NamedQuery(
                        name = "Service.findAll",
                        query = "SELECT s FROM Service s"
                )
        }
)
public class Service implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idService", nullable = false)
    private int idService;

    @Column(name= "serviceType", nullable = false)
    private int serviceType;

    @Column(name="minutes", nullable = false)
    private int minutes = 0;

    @Column(name="extraMinutesFee", nullable = false)
    private float extraMinutesFee = 0;

    @Column(name="sms", nullable = false)
    private int sms = 0;

    @Column(name="extraSmsFee", nullable = false)
    private float extraSmsFee = 0;

    @Column(name="giga", nullable = false)
    private int giga = 0;

    @Column(name="extraGigaFee", nullable = false)
    private float extraGigaFee = 0;

    @ManyToMany(mappedBy = "services",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
   private List<Package> packages  = new ArrayList<>();

    @ManyToMany(mappedBy = "services",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<Schedule> schedules = new ArrayList<>();

    public Service() {
    }

    public Service(int serviceType, Integer minutes, Float extraMinutesFee, Integer sms, Float extraSmsFee, Integer giga, Float extraGigaFee) {
        this.serviceType = serviceType;
        this.minutes = minutes != null ? minutes : 0;
        this.extraMinutesFee = extraMinutesFee!= null ? extraMinutesFee : 0;
        this.sms = sms != null ? sms : 0;
        this.extraSmsFee = extraSmsFee != null ? extraSmsFee : 0;
        this.giga = giga != null ? giga : 0;
        this.extraGigaFee = extraGigaFee != null ? extraGigaFee : 0;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public int getServiceType() {
        return serviceType;
    }

    public String getServiceTypeHumanReadable() {
        switch (serviceType) {
            case 0:
                return "Fixed Phone";
            case 1:
                return "Fixed Internet";
            case 2:
                return "Mobile Phone";
            case 3:
                return "Mobile Internet";
            default:
                return "";
        }
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public float getExtraMinutesFee() {
        return extraMinutesFee;
    }

    public void setExtraMinutesFee(float extraMinutesFee) {
        this.extraMinutesFee = extraMinutesFee;
    }

    public int getSms() {
        return sms;
    }

    public void setSms(int sms) {
        this.sms = sms;
    }

    public float getExtraSmsFee() {
        return extraSmsFee;
    }

    public void setExtraSmsFee(float extraSmsFee) {
        this.extraSmsFee = extraSmsFee;
    }

    public int getGiga() {
        return giga;
    }

    public void setGiga(int giga) {
        this.giga = giga;
    }

    public float getExtraGigaFee() {return extraGigaFee;}

    public void setExtraGigaFee(float extraGigaFee) {
        this.extraGigaFee = extraGigaFee;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }
}
