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
@Table(name = "sequence_local")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SequenceLocal.findAll", query = "SELECT s FROM SequenceLocal s"),
    @NamedQuery(name = "SequenceLocal.findById", query = "SELECT s FROM SequenceLocal s WHERE s.id = :id"),
    @NamedQuery(name = "SequenceLocal.findBySequence", query = "SELECT s FROM SequenceLocal s WHERE s.sequence = :sequence"),
    @NamedQuery(name = "SequenceLocal.findByInserted", query = "SELECT s FROM SequenceLocal s WHERE s.inserted = :inserted"),
    @NamedQuery(name = "SequenceLocal.findByOwner", query = "SELECT s FROM SequenceLocal s WHERE s.owner = :owner"),
    @NamedQuery(name = "SequenceLocal.findByDpclass", query = "SELECT s FROM SequenceLocal s WHERE s.dpclass = :dpclass")})
public class SequenceLocal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "sequence")
    private String sequence;
    @Basic(optional = false)
    @Column(name = "inserted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inserted;
    @Column(name = "owner")
    private Integer owner;
    @Column(name = "dpclass")
    private String dpclass;

    public SequenceLocal() {
    }

    public SequenceLocal(Integer id) {
        this.id = id;
    }

    public SequenceLocal(Integer id, String sequence, Date inserted) {
        this.id = id;
        this.sequence = sequence;
        this.inserted = inserted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public Date getInserted() {
        return inserted;
    }

    public void setInserted(Date inserted) {
        this.inserted = inserted;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public String getDpclass() {
        return dpclass;
    }

    public void setDpclass(String dpclass) {
        this.dpclass = dpclass;
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
        if (!(object instanceof SequenceLocal)) {
            return false;
        }
        SequenceLocal other = (SequenceLocal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DB.SequenceLocal[ id=" + id + " ]";
    }
    
}
