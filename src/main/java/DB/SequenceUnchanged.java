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
@Table(name = "sequence_unchanged")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SequenceUnchanged.findAll", query = "SELECT s FROM SequenceUnchanged s"),
    @NamedQuery(name = "SequenceUnchanged.findById", query = "SELECT s FROM SequenceUnchanged s WHERE s.id = :id"),
    @NamedQuery(name = "SequenceUnchanged.findBySequence", query = "SELECT s FROM SequenceUnchanged s WHERE s.sequence = :sequence"),
    @NamedQuery(name = "SequenceUnchanged.findByInserted", query = "SELECT s FROM SequenceUnchanged s WHERE s.inserted = :inserted"),
    @NamedQuery(name = "SequenceUnchanged.findByOwner", query = "SELECT s FROM SequenceUnchanged s WHERE s.owner = :owner")})
public class SequenceUnchanged implements Serializable {
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
    @Basic(optional = false)
    @Column(name = "owner")
    private int owner;

    public SequenceUnchanged() {
    }

    public SequenceUnchanged(Integer id) {
        this.id = id;
    }

    public SequenceUnchanged(Integer id, String sequence, Date inserted, int owner) {
        this.id = id;
        this.sequence = sequence;
        this.inserted = inserted;
        this.owner = owner;
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

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
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
        if (!(object instanceof SequenceUnchanged)) {
            return false;
        }
        SequenceUnchanged other = (SequenceUnchanged) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DB.SequenceUnchanged[ id=" + id + " ]";
    }
    
}
