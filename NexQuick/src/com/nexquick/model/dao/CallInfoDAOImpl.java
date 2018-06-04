package com.nexquick.model.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nexquick.model.vo.CallInfo;

public class CallInfoDAOImpl implements CallInfoDAO {

	private SqlSession sqlSession;
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	@Override
	public boolean createCall(CallInfo callInfo) {
		return sqlSession.insert("callInfo.createCall", callInfo)>0;
	}

	@Override
	public boolean updateCall(CallInfo callInfo) {
		return sqlSession.update("callInfo.updateCall", callInfo)>0;
	}

	@Override
	public boolean deleteCall(int callNum) {
		return sqlSession.delete("callInfo.deleteCall", callNum)>0;
	}

	@Override
	public CallInfo selectCall(int callNum) {
		return sqlSession.selectOne("callInfo.selectCallByCnum", callNum);
	}
	
	@Override
	public CallInfo selectCall(String csId) {
		return sqlSession.selectOne("callInfo.selectCallByCSId", csId);
	}

	@Override
	public List<CallInfo> selectCallList() {
		return sqlSession.selectList("callInfo.selectCallList");
	}

	@Override
	public List<CallInfo> selectCallList(String csId, int qpId, int deliveryStatus) {
		HashMap<String, Object> condition = new HashMap<>();
		condition.put("csId", csId);
		condition.put("qpId", qpId);
		condition.put("deliveryStatus", deliveryStatus);
		return sqlSession.selectList("callInfo.selectCallListByCondition", condition);
	}

	@Override
	public void deletePastCalls() {
		sqlSession.delete("callInfo.deletePastCalls");
	}


}