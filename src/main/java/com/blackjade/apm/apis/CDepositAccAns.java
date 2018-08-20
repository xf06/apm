package com.blackjade.apm.apis;

import java.util.UUID;

import com.blackjade.apm.apis.ComStatus.DepositAccStatus;
import com.blackjade.apm.apis.ComStatus.DepositOrdStatus;

//cDepositAcc	0x7104	{requestid, clientid, pnsid, pnsgid}	HTTP

public class CDepositAccAns {

	private String messageid;
	private UUID requestid;
	private int clientid;
	private UUID oid;
	private int pnsid;
	private int pnsgid;
	private long quant;
	private String tranid;
	private DepositOrdStatus conlvl;
	private DepositAccStatus status;

	public CDepositAccAns() {
	}

	public CDepositAccAns(UUID requestid) {
		this.messageid = "7104";
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

	public DepositAccStatus getStatus() {
		return status;
	}

	public void setStatus(DepositAccStatus status) {
		this.status = status;
	}
	
	public DepositOrdStatus getConlvl() {
		return conlvl;
	}

	public void setConlvl(DepositOrdStatus conlvl) {
		this.conlvl = conlvl;
	}

	@Override
	public String toString() {
		return "CDepositAccAns [messageid=" + messageid + ", requestid=" + requestid + ", clientid=" + clientid
				+ ", oid=" + oid + ", pnsid=" + pnsid + ", pnsgid=" + pnsgid + ", quant=" + quant + ", tranid=" + tranid
				+ ", conlvl=" + conlvl + ", status=" + status + "]";
	}

}
