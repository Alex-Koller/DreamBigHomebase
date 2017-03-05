package com.dreambig.homebase.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.dreambig.homebase.entity.DeviceType;
import com.dreambig.homebase.entity.PasswordClaim;
import com.dreambig.homebase.entity.PasswordClaimStatus;
import com.dreambig.homebase.entity.UserTicket;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class DataAccess {
	
	//password claims
	
	public int deleteAllPasswordClaims(Date olderThan, PasswordClaimStatus inStatus) {
		int nUpdated = 0;
		EntityManager em = getEntityManager();
		
		try {
			String queryStr = "delete from " + PasswordClaim.class.getName() + " p where p.lastUpdate < :olderThan and p.passwordClaimStatus = :inStatus";
			nUpdated = em.createQuery(queryStr).setParameter("olderThan", olderThan).setParameter("inStatus", inStatus).executeUpdate();			
		} finally {
			em.close();
		}
	
		return nUpdated;
	}
	
	public void storePasswordClaim(PasswordClaim passwordClaim) {
    	EntityManager em = getEntityManager();
    	
    	try {
    	   em.persist(passwordClaim);
    	} finally {
    	   em.close();
    	}  		
	}
	
    public PasswordClaim getPasswordClaim(Long claimId) {
    	EntityManager em = getEntityManager();
    	PasswordClaim ret;
    	
    	try {
    	   ret = em.find(PasswordClaim.class, claimId);
    	} finally {
    	   em.close();
    	}
    	
    	return ret;
    }	
	
	public List<PasswordClaim> getPasswordClaims() {
		EntityManager em = getEntityManager();
		List<PasswordClaim> ret;
		
		try {
			String queryStr = "select r from " + PasswordClaim.class.getName() + " r";
			ret =  em.createQuery(queryStr).getResultList();			
		} finally {
			em.close();
		}
		
		return ret;
	}    
	
    public void addCodeToPasswordClaim(Long claimId, String code) throws PasswordClaimNotFoundException {
    	EntityManager em = getEntityManager();
    	
    	try {
    	  PasswordClaim passwordClaim = em.find(PasswordClaim.class, claimId);
    	  if(passwordClaim == null) {
    		  throw new PasswordClaimNotFoundException("Claim id " + claimId + " not found");
    	  }
   	      passwordClaim.setAssociatedCode(code);
   	      passwordClaim.setPasswordClaimStatus(PasswordClaimStatus.CODE_ADDED);
   	      passwordClaim.setLastUpdate(new Date());
   	      em.merge(passwordClaim);
    	} finally {
    	  em.close();
    	}    	
    }	
    
    public void setStatusToPasswordClaim(Long claimId, PasswordClaimStatus status) throws PasswordClaimNotFoundException {
   
    	EntityManager em = getEntityManager();
    	
    	try {
    	  PasswordClaim passwordClaim = em.find(PasswordClaim.class, claimId);
    	  if(passwordClaim == null) {
    		  throw new PasswordClaimNotFoundException("Claim id " + claimId + " not found");
    	  }

   	      passwordClaim.setPasswordClaimStatus(status);
   	      passwordClaim.setLastUpdate(new Date());
   	      em.merge(passwordClaim);
    	} finally {
    	  em.close();
    	}      	
    	
    }
    
	//user tickets
	
	public void upadateLatestNewsTimestamp(String userId, Long timestamp) {
    	
		EntityManager em = getEntityManager();
    	try {	    		 
    		UserTicket userTicket = em.find(UserTicket.class, userId);
    		userTicket.setLastNewsTimestamp(timestamp);
	    	em.merge(userTicket);	    	     
    	} finally {
    		em.close();
    	}    	     
    	     		
	}
	
	public void removeUserTicketInTransaction(UserTicket userTicket) {

    	EntityManager em = getEntityManager();
    	
		try {
	        em.getTransaction().begin();
	        em.remove(em.find(UserTicket.class, userTicket.getUserId()));
	        em.getTransaction().commit();
		}
		finally {
	    	em.close();
	    }		
		
	}
	
	public List<UserTicket> getUserTickets() {
		EntityManager em = getEntityManager();
		List<UserTicket> ret;
		
		try {
			String queryStr = "select r from " + UserTicket.class.getName() + " r";
			ret =  em.createQuery(queryStr).getResultList();			
		} finally {
			em.close();
		}
		
		return ret;
	}
	
	public List<UserTicket> getUserTicketByTokenAndType(String token, DeviceType deviceType) {
		
    	EntityManager em = getEntityManager();
    	List<UserTicket> ret;
    	
    	try {
    		 String queryStr = String.format("select r from " + UserTicket.class.getName() + 
    			        " r where r.token = '%s' and r.deviceType = '%s'", token, deviceType.toString());
    	     ret = em.createQuery(queryStr).getResultList();    		
    	} finally {
    	   em.close();
    	}
    	
    	return ret;		
	}
	
    public UserTicket getUserTicket(String userId) {
    	EntityManager em = getEntityManager();
    	UserTicket ret;
    	
    	try {
    	   ret = em.find(UserTicket.class, userId);
    	} finally {
    	   em.close();
    	}
    	
    	return ret;
    }

    public void addUserTicket(UserTicket userTicket) {
    	EntityManager em = getEntityManager();
    	
    	try {
    	   em.persist(userTicket);
    	} finally {
    	   em.close();
    	}    	 	
    }
    
    public void updateUserTicket(UserTicket newUserTicket) {
    	EntityManager em = getEntityManager();
    	
    	try {
   	      UserTicket existingTicket = em.find(UserTicket.class, newUserTicket.getUserId());
   	      existingTicket.updateFrom(newUserTicket);
   	      em.merge(existingTicket);
    	} finally {
    	  em.close();
    	}    	
    }

    //util methods
    private static EntityManager getEntityManager() {
        return EMF.get().createEntityManager();
    }   
    
	//helper classes
	public static class PasswordClaimNotFoundException extends Exception {
		  
		public PasswordClaimNotFoundException(String message) {
		  super(message);
		}
		  
	}
}
