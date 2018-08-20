package com.blackjade.apm.apis;

import java.util.UUID;

import com.blackjade.apm.apis.ComStatus.DepositAccStatus;
import com.blackjade.apm.apis.ComStatus.DepositOrdStatus;

//cDepositAcc	0x7103	{requestid, clientid, pnsid, pnsgid}	HTTP

public class CDepositAcc {

	private String messageid;
	private UUID requestid;
	private int clientid;
	private UUID oid;
	private int pnsid;
	private int pnsgid;
	private long quant;
	private String tranid;
	private DepositOrdStatus conlvl;

	public DepositAccStatus reviewData() {

		if (!this.messageid.equals("7105"))
			return ComStatus.DepositAccStatus.WRONG_MSGID;

		if (this.requestid == null)
			return ComStatus.DepositAccStatus.IN_MSG_ERR;

		if (this.clientid <= 0)
			return ComStatus.DepositAccStatus.IN_MSG_ERR;

		if (this.quant <= 0)
			return ComStatus.DepositAccStatus.IN_MSG_ERR;

		return ComStatus.DepositAccStatus.SUCCESS;
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

	public DepositOrdStatus getConlvl() {
		return conlvl;
	}

	public void setConlvl(DepositOrdStatus conlvl) {
		this.conlvl = conlvl;
	}

	@Override
	public String toString() {
		return "CDepositAcc [messageid=" + messageid + ", requestid=" + requestid + ", clientid=" + clientid + ", oid="
				+ oid + ", pnsid=" + pnsid + ", pnsgid=" + pnsgid + ", quant=" + quant + ", tranid=" + tranid
				+ ", conlvl=" + conlvl + "]";
	}

}
