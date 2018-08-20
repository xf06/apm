package com.blackjade.apm.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.blackjade.apm.domain.AccRow;

@Component
public interface AccDao {

	// select Accrow
	public AccRow selectAccRow(@Param(value = "cid") int cid, @Param(value = "pnsgid") int pnsgid,
			@Param(value = "pnsid") int pnsid);

	// update for publish and deal
	public int updatePubAccRow(@Param(value = "cid") int cid, @Param(value = "pnsgid") int pnsgid,
			@Param(value = "pnsid") int pnsid, @Param(value = "margin") long margin,
			@Param(value = "freemargin") long freemargin);
	
	// update for payconfirm
	// update buyside acc
	public int updateBSAccRow(@Param(value = "cid") int cid, @Param(value = "pnsgid") int pnsgid,
			@Param(value = "pnsid") int pnsid, @Param(value="balance") long balance, @Param(value = "freemargin") long freemargin,
			@Param(value = "pnl") long pnl);
	
	// update sellside acc
	public int updateSSAccRow(@Param(value = "cid") int cid, @Param(value = "pnsgid") int pnsgid,
			@Param(value = "pnsid") int pnsid, @Param(value="balance") long balance, @Param(value = "margin") long margin,
			@Param(value = "pnl") long pnl);
	
	
	// update for cancel
	// update sellside deal acc
	public int updateSDCanAccRow(@Param(value = "cid") int cid, @Param(value = "pnsgid") int pnsgid,
			@Param(value = "pnsid") int pnsid, @Param(value = "margin") long margin,
			@Param(value = "freemargin") long freemargin);
	
	// update sellside pub acc
	public int updateSPCanAccRow(@Param(value = "cid") int cid, @Param(value = "pnsgid") int pnsgid,
			@Param(value = "pnsid") int pnsid, @Param(value = "margin") long margin,
			@Param(value = "freemargin") long freemargin);

	// update for depoist
	public int updateDepositAccRow();
	
	public int updateDepositAccRowConfirm();

	// update for withdraw
	public int updateWithdrawAccRow();
	
	public int updateWithdrawAccRowConfirm();
	
}
