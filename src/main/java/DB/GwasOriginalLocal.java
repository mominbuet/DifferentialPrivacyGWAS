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
@Table(name = "gwas_original_local_copy")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GwasOriginalLocal.findAll", query = "SELECT g FROM GwasOriginalLocal g"),
    @NamedQuery(name = "GwasOriginalLocal.findById", query = "SELECT g FROM GwasOriginalLocal g WHERE g.id = :id"),
    @NamedQuery(name = "GwasOriginalLocal.findBySnpid", query = "SELECT g FROM GwasOriginalLocal g WHERE g.snpid = :snpid"),
    @NamedQuery(name = "GwasOriginalLocal.findBySnpidCasecontrol", query = "SELECT g FROM GwasOriginalLocal g WHERE g.snpid = :snpid and g.casecontrol = :casecontrol"),
    @NamedQuery(name = "GwasOriginalLocal.findByCasecontrol", query = "SELECT g FROM GwasOriginalLocal g WHERE g.casecontrol = :casecontrol"),
    @NamedQuery(name = "GwasOriginalLocal.findByMajormajor", query = "SELECT g FROM GwasOriginalLocal g WHERE g.majormajor = :majormajor"),
    @NamedQuery(name = "GwasOriginalLocal.findByMajorminor", query = "SELECT g FROM GwasOriginalLocal g WHERE g.majorminor = :majorminor"),
    @NamedQuery(name = "GwasOriginalLocal.findByMinormajor", query = "SELECT g FROM GwasOriginalLocal g WHERE g.minormajor = :minormajor"),
    @NamedQuery(name = "GwasOriginalLocal.findByMinorminor", query = "SELECT g FROM GwasOriginalLocal g WHERE g.minorminor = :minorminor"),
    @NamedQuery(name = "GwasOriginalLocal.findByUpdated", query = "SELECT g FROM GwasOriginalLocal g WHERE g.updated = :updated")})
public class GwasOriginalLocal implements Serializable {

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
    private int majormajor;
    @Basic(optional = false)
    @Column(name = "majorminor")
    private int majorminor;
    @Basic(optional = false)
    @Column(name = "minormajor")
    private int minormajor;
    @Basic(optional = false)
    @Column(name = "minorminor")
    private int minorminor;
    @Basic(optional = false)
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public GwasOriginalLocal() {
    }

    public GwasOriginalLocal(Integer id) {
        this.id = id;
    }

    public GwasOriginalLocal(Integer id, String snpid, int casecontrol, int majormajor, int majorminor, int minormajor, int minorminor, Date updated, String dpClass) {
        this.id = id;
        this.snpid = snpid;
        this.casecontrol = casecontrol;
        this.majormajor = majormajor;
        this.majorminor = majorminor;
        this.minormajor = minormajor;
        this.minorminor = minorminor;
        this.updated = updated;
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

    public int getMajormajor() {
        return majormajor;
    }

    public void setMajormajor(int majormajor) {
        this.majormajor = majormajor;
    }

    public int getMajorminor() {
        return majorminor;
    }

    public void setMajorminor(int majorminor) {
        this.majorminor = majorminor;
    }

    public int getMinormajor() {
        return minormajor;
    }

    public void setMinormajor(int minormajor) {
        this.minormajor = minormajor;
    }

    public int getMinorminor() {
        return minorminor;
    }

    public void setMinorminor(int minorminor) {
        this.minorminor = minorminor;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
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
        if (!(object instanceof GwasOriginalLocal)) {
            return false;
        }
        GwasOriginalLocal other = (GwasOriginalLocal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DB.GwasOriginalLocal[ id=" + id + " ]";
    }

}
