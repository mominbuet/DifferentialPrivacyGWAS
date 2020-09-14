/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author azizmma
 */
@Entity
@Table(name = "gwas_local_laplace_5")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GwasLocalLaplace.findAll", query = "SELECT g FROM GwasLocalLaplace g"),
    @NamedQuery(name = "GwasLocalLaplace.findById", query = "SELECT g FROM GwasLocalLaplace g WHERE g.id = :id"),
    @NamedQuery(name = "GwasLocalLaplace.findBySnpid", query = "SELECT g FROM GwasLocalLaplace g WHERE g.snpid = :snpid"),
    @NamedQuery(name = "GwasLocalLaplace.findByCasecontrol", query = "SELECT g FROM GwasLocalLaplace g WHERE g.casecontrol = :casecontrol"),
    @NamedQuery(name = "GwasLocalLaplace.findBySnpidCasecontrol", query = "SELECT g FROM GwasLocalLaplace g WHERE g.snpid = :snpid and g.casecontrol = :casecontrol and g.dpClass = :dpClass"),
    @NamedQuery(name = "GwasLocalLaplace.findByMajormajor", query = "SELECT g FROM GwasLocalLaplace g WHERE g.majormajor = :majormajor"),
    @NamedQuery(name = "GwasLocalLaplace.findByMajorminor", query = "SELECT g FROM GwasLocalLaplace g WHERE g.majorminor = :majorminor"),
    @NamedQuery(name = "GwasLocalLaplace.findByMinormajor", query = "SELECT g FROM GwasLocalLaplace g WHERE g.minormajor = :minormajor"),
    @NamedQuery(name = "GwasLocalLaplace.findByMinorminor", query = "SELECT g FROM GwasLocalLaplace g WHERE g.minorminor = :minorminor"),
    @NamedQuery(name = "GwasLocalLaplace.findByUpdated", query = "SELECT g FROM GwasLocalLaplace g WHERE g.updated = :updated"),
    @NamedQuery(name = "GwasLocalLaplace.findByDpClass", query = "SELECT g FROM GwasLocalLaplace g WHERE g.dpClass = :dpClass")})
public class GwasLocalLaplace implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "snpid")
    private String snpid;
    @Basic(optional = false)
    @Column(name = "casecontrol")
    private int casecontrol;
    @Basic(optional = false)
    @Column(name = "majormajor")
    private double majormajor;
    @Basic(optional = false)
    @Column(name = "majorminor")
    private double majorminor;
    @Basic(optional = false)
    @Column(name = "minormajor")
    private double minormajor;
    @Basic(optional = false)
    @Column(name = "minorminor")
    private double minorminor;
    @Basic(optional = false)
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    @Basic(optional = false)
    @Column(name = "dp_class")
    private String dpClass;

    public GwasLocalLaplace() {
    }

    public GwasLocalLaplace(Integer id) {
        this.id = id;
    }

    public GwasLocalLaplace(Integer id, String snpid, int casecontrol, double majormajor, double majorminor, double minormajor, double minorminor, Date updated, String dpClass) {
        this.id = id;
        this.snpid = snpid;
        this.casecontrol = casecontrol;
        this.majormajor = majormajor;
        this.majorminor = majorminor;
        this.minormajor = minormajor;
        this.minorminor = minorminor;
        this.updated = updated;
        this.dpClass = dpClass;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSnpid() {
        return snpid;
    }

    public void setSnpid(String snpid) {
        this.snpid = snpid;
    }

    public int getCasecontrol() {
        return casecontrol;
    }

    public void setCasecontrol(int casecontrol) {
        this.casecontrol = casecontrol;
    }

    public double getMajormajor() {
        return majormajor;
    }

    public void setMajormajor(double majormajor) {
        this.majormajor = majormajor;
    }

    public double getMajorminor() {
        return majorminor;
    }

    public void setMajorminor(double majorminor) {
        this.majorminor = majorminor;
    }

    public double getMinormajor() {
        return minormajor;
    }

    public void setMinormajor(double minormajor) {
        this.minormajor = minormajor;
    }

    public double getMinorminor() {
        return minorminor;
    }

    public void setMinorminor(double minorminor) {
        this.minorminor = minorminor;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getDpClass() {
        return dpClass;
    }

    public void setDpClass(String dpClass) {
        this.dpClass = dpClass;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GwasLocalLaplace)) {
            return false;
        }
        GwasLocalLaplace other = (GwasLocalLaplace) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DB.GwasLocalLaplace[ id=" + id + " ]";
    }
    
}
