package com.blackjade.apm.apis;

import java.util.UUID;

import com.blackjade.apm.apis.ComStatus;
import com.blackjade.apm.apis.ComStatus.PCancelStatus;

//cPCancel	0x701D	{requestid, clientid, oid, cid, side, pnsoid, poid, pnsid, pnsgid, amount}	HTTP
public class CPCancel {

	private String messageid;
	private UUID requestid;
	private int clientid;
	//private UUID oid; // no need 
	//private int cid;  // no need
	private char side; // <B or S>
	private UUID pnsoid;
	private int poid; // product owner id
	private int pnsid;
	private int pnsgid;
	private long amount;

	public PCancelStatus reviewData() {
		
		if (!this.messageid.equals("701D"))
			return ComStatus.PCancelStatus.WRONG_MSGID;

		if (this.requestid == null)
			return ComStatus.PCancelStatus.IN_MSG_ERR;

		if (this.clientid <= 0)
			return ComStatus.PCancelStatus.IN_MSG_ERR;

		if (('S' != this.side) && ('B' != this.side))
			return ComStatus.PCancelStatus.IN_MSG_ERR;

		if (this.pnsoid == null)
			return ComStatus.PCancelStatus.IN_MSG_ERR;

		if (this.poid <= 0)
			return ComStatus.PCancelStatus.IN_MSG_ERR;

		if (this.amount<= 0)
			return ComStatus.PCancelStatus.IN_MSG_ERR;
			
		// logic check
		if(this.clientid!=this.poid)
			return ComStatus.PCancelStatus.IN_MSG_ERR;
				
		
		return ComStatus.PCancelStatus.SUCCESS;
	}
	
	public String getMessageid() {
		return messageid;
	}

	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}

	public UUID getRequestid() {
		return requestid;
	}

	public void setRequestid(UUID requestid) {
		this.requestid = requestid;
	}

	public int getClientid() {
		return clientid;
	}

	public void setClientid(int clientid) {
		this.clientid = clientid;
	}

//	public UUID getOid() {
//		return oid;
//	}
//
//	public void setOid(UUID oid) {
//		this.oid = oid;
//	}
//
//	public int getCid() {
//		return cid;
//	}
//
//	public void setCid(int cid) {
//		this.cid = cid;
//	}

	public char getSide() {
		return side;
	}

	public void setSide(char side) {
		this.side = side;
	}

	public UUID getPnsoid() {
		return pnsoid;
	}

	public void setPnsoid(UUID pnsoid) {
		this.pnsoid = pnsoid;
	}

	public int getPoid() {
		return poid;
	}

	public void setPoid(int poid) {
		this.poid = poid;
	}

	public int getPnsid() {
		return pnsid;
	}

	public void setPnsid(int pnsid) {
		this.pnsid = pnsid;
	}

	public int getPnsgid() {
		return pnsgid;
	}

	public void setPnsgid(int pnsgid) {
		this.pnsgid = pnsgid;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

}
