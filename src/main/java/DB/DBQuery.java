/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author azizmma
 */
public class DBQuery {

    String opuname = "cs.umanitoba.ca_DPBinPacking_jar_1.0.0PU";

    public GwasPlaintext getFromSnip(String snip) {
        GwasPlaintext res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasPlaintext.findBySnpid", GwasPlaintext.class)
                    .setParameter("snpid", snip).setMaxResults(1).getResultList().get(0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<GwasPlaintext> getAllSNP() {
        List<GwasPlaintext> res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasPlaintext.findAll", GwasPlaintext.class)
                    .getResultList();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<GwasOriginal> getAllOriginalSNP() {
        List<GwasOriginal> res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasOriginal.findAll", GwasOriginal.class)
                    //.setParameter("casecontrol", 0)
                    .getResultList();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

//    public List<GwasOriginal> getAllOriginalSNP(String class_name) {
//        List<GwasOriginal> res = null;
//        try {
//            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
//            EntityManager em = emf.createEntityManager();
//            res = em.createNamedQuery("GwasOriginal.findAll", GwasOriginal.class).setParameter("dpClass", class_name)
//                    //.setParameter("casecontrol", 0)
//                    .getResultList();
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//        }
//        return res;
//    }
    public List<GwasLocal> getAllLocalSNP(String class_name) {
        List<GwasLocal> res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasLocal.findByDpClass", GwasLocal.class).setParameter("dpClass", class_name)//.setParameter("casecontrol", 0)
                    .getResultList();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<GwasLocal> getAllLocalSNP() {
        List<GwasLocal> res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasLocal.findAll", GwasLocal.class)
                    .getResultList();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<GwasLocalLaplace> getAllLocalLapSNP(String class_name) {
        List<GwasLocalLaplace> res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasLocalLaplace.findByDpClass", GwasLocalLaplace.class).setParameter("dpClass", class_name)//.setParameter("casecontrol", 0)
                    .getResultList();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<GwasLocalLaplace> getAllLocalLapSNP() {
        List<GwasLocalLaplace> res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasLocalLaplace.findAll", GwasLocalLaplace.class)//.setParameter("casecontrol", 0)
                    .getResultList();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public GwasOriginalLocal getFromOriginalLocalSnip(String snip, int casecontrol) {
        GwasOriginalLocal res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasOriginalLocal.findBySnpidCasecontrol", GwasOriginalLocal.class)
                    .setParameter("snpid", snip)
                    .setParameter("casecontrol", casecontrol)
//                    .setParameter("dpClass", className)
                    .setMaxResults(1).getResultList().get(0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public GwasOriginal getFromOriginalSnip(String snip, int casecontrol) {
        GwasOriginal res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasOriginal.findBySnpidCasecontrol", GwasOriginal.class)
                    .setParameter("snpid", snip)
                    .setParameter("casecontrol", casecontrol)
                    .setMaxResults(1).getResultList().get(0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }


    public GwasLocal getFromLocalSnip(String snip, int casecontrol, String class_name) {
        GwasLocal res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasLocal.findBySnpidCasecontrol", GwasLocal.class)
                    .setParameter("snpid", snip).setParameter("dpClass", class_name)
                    .setParameter("casecontrol", casecontrol)
                    .setMaxResults(1).getResultList().get(0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public GwasLocalLaplace getFromLocalLapSnip(String snip, int casecontrol, String class_name) {
        GwasLocalLaplace res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasLocalLaplace.findBySnpidCasecontrol", GwasLocalLaplace.class)
                    .setParameter("snpid", snip).setParameter("dpClass", class_name)
                    .setParameter("casecontrol", casecontrol)
                    .setMaxResults(1).getResultList().get(0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public GwasPlaintext getFromSnip(String snip, int casecontrol) {
        GwasPlaintext res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasPlaintext.findBySnpidCasecontrol", GwasPlaintext.class)
                    .setParameter("snpid", snip)
                    .setParameter("casecontrol", casecontrol)
                    .setMaxResults(1).getResultList().get(0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public <T> T insertGeneric(T p) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            EntityTransaction entr = em.getTransaction();
            entr.begin();
            em.persist(p);
            entr.commit();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return p;
    }

    public GwasOriginal getGwasOriginalbyID(String snpid) {
        GwasOriginal res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasOriginal.findBySnpid", GwasOriginal.class)
                    .setParameter("snpid", snpid)
                    .setMaxResults(1).getResultList().get(0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;

    }

    public List<GwasOriginalLocal> getAllGwasOriginalLocalSNP() {
        
        List<GwasOriginalLocal> res = null;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(opuname);
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("GwasOriginalLocal.findAll", GwasOriginalLocal.class)
                    //.setParameter("casecontrol", 0)
                    .getResultList();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

}
