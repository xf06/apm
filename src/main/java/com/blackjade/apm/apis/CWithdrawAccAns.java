package com.blackjade.apm.apis;

import java.util.UUID;

import com.blackjade.apm.apis.ComStatus.WithdrawAccStatus;
import com.blackjade.apm.apis.ComStatus.WithdrawOrdStatus;

//  cWithdrawAccAns	0x7106	{requestid, clientid, oid, pnsid, pnsgid, status}	HTTP

public class CWithdrawAccAns {

	private String messageid;
	private UUID requestid;
	private int clientid;
	private UUID oid;
	private int pnsid;
	private int pnsgid;
	private long quant;
	private String tranid;
	private WithdrawOrdStatus conlvl;
	private WithdrawAccStatus status;
	
	public CWithdrawAccAns() {		
	}

	public CWithdrawAccAns(UUID requestid) {
		this.messageid = "7106";
		this.requestid = requestid;
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

	public UUID getOid() {
		return oid;
	}

	public void setOid(UUID oid) {
		this.oid = oid;
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

	public long getQuant() {
		return quant;
	}

	public void setQuant(long quant) {
		this.quant = quant;
	}
	
	public String getTranid() {
		return tranid;
	}

	public void setTranid(String tranid) {
		this.tranid = tranid;
	}

	public WithdrawOrdStatus getConlvl() {
		return conlvl;
	}

	public void setConlvl(WithdrawOrdStatus conlvl) {
		this.conlvl = conlvl;
	}
	
	public WithdrawAccStatus getStatus() {
		return status;
	}

	public void setStatus(WithdrawAccStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CWithdrawAccAns [messageid=" + messageid + ", requestid=" + requestid + ", clientid=" + clientid
				+ ", oid=" + oid + ", pnsid=" + pnsid + ", pnsgid=" + pnsgid + ", quant=" + quant + ", status=" + status
				+ "]";
	}

}
