package com.nexquick.System.Allocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexquick.System.FireBaseMessaging;
import com.nexquick.model.dao.CSInfoDAO;
import com.nexquick.model.vo.Address;
import com.nexquick.model.vo.CallInfo;
import com.nexquick.model.vo.OnDelivery;
import com.nexquick.model.vo.QPPosition;
import com.nexquick.service.account.QPPositionService;
import com.nexquick.service.call.CallManagementService;
import com.nexquick.service.call.CallSelectListService;
import com.nexquick.service.parsing.AddressTransService;

@Service
public class AllocationThread {
	
	AllocationQueue allocationQueue = AllocationQueue.getInstance();

	private AddressTransService addressTransService;
	@Autowired
	public void setAddressTransService(AddressTransService addressTransService) {
		this.addressTransService = addressTransService;
	}
	
	private QPPositionService qpPositionService;
	@Autowired
	public void setQpPositionService(QPPositionService qpPositionService) {
		this.qpPositionService = qpPositionService;
	}
	
	private CallManagementService callManagementService;
	@Autowired
	public void setCallManagementService(CallManagementService callManagementService) {
		this.callManagementService = callManagementService;
	}
	
	private CallSelectListService callSelectService;
	@Autowired
	public void setCallSelectService(CallSelectListService callSelectService) {
		this.callSelectService = callSelectService;
	}

	private CSInfoDAO csInfoDao;
	@Autowired
	public void setCsInfoDao(CSInfoDAO csInfoDao) {
		this.csInfoDao = csInfoDao;
	}
	
	FireBaseMessaging fireBaseMessaging = FireBaseMessaging.getInstance();


	public AllocationThread() {
		Runnable r = new allocateCall();
		Thread t = new Thread(r);
		t.start();
	}
	
	
	class allocateCall implements Runnable{
		
		
		@Override
		public void run() {
			System.out.println("스레드가 생성되었음");
			while(!Thread.currentThread().isInterrupted()) {
				allocate(allocationQueue.poll());
			}
		}
	}


	private void allocate(Map<String, Object> map) {
		List<QPPosition> qpList = null;
		System.out.println("하나 뽑아옴");
		int repeat = (int) map.get("repeat");
		CallInfo callInfo = (CallInfo) map.get("callInfo");
		List<OnDelivery> orders = callSelectService.orderListByCallNum(callInfo.getCallNum());
		StringBuilder msgBd = new StringBuilder();
		if(orders.get(0).getUrgent()==1) {
			msgBd.append("급/");
		}
		msgBd.append("픽/").append(orders.get(0).getSenderAddress());
		
		for(int i=0; i<orders.size(); i++) {
			msgBd.append("/착");
			if(orders.size()!=1) {
				msgBd.append(" ").append(i+1);
			}
			msgBd.append("/").append(orders.get(i).getReceiverAddress());
			msgBd.append(orders.get(i).getFreightList());
		}
		msgBd.append("/");
		msgBd.append(callInfo.getTotalPrice()).append("원@");
		msgBd.append(callInfo.getCallNum());
		String token = csInfoDao.selectCSDevice(callInfo.getCsId());
		if(repeat==3) {
			fireBaseMessaging.sendMessage(token, callInfo.getCallNum()+"번 콜의 배차에 실패했습니다.");
			callInfo.setDeliveryStatus(-1);
			callManagementService.updateCall(callInfo);
			return;
		}
		String addrStr = callInfo.getSenderAddress()+" "+callInfo.getSenderAddressDetail();
		Address addr = addressTransService.getAddress(addrStr);
		String hCode = addr.gethCode();
		System.out.println(hCode);
		if (hCode!=null) {
			qpList = qpPositionService.selectQPListByHCode(addr);
		}else {
			String bCode = addr.getbCode();
			qpList = qpPositionService.selectQPListByBCode(addr);
		}
		
		if (qpList.size()!=0) {
			System.out.println("repeat : "+repeat);
			for(QPPosition qp : qpList) {
				System.out.println(qp.getQpId());
				//callInfo.setQpId(qp.getQpId());
				//callManagementService.updateCall(callInfo);
				fireBaseMessaging.sendMessage(qp.getConnectToken(), msgBd.toString());
				
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (callSelectService.selectCallInfo(callInfo.getCallNum()).getQpId()!=0) {
					fireBaseMessaging.sendMessage(token, callInfo.getCallNum()+"번 콜의 배차가 완료됐습니다.");
					callInfo.setDeliveryStatus(2);
					callManagementService.updateCall(callInfo);
					break;
				}
			}
			if (callSelectService.selectCallInfo(callInfo.getCallNum()).getQpId()==0) {
				allocationQueue.offer(callInfo, repeat+1);
				System.out.println("다시 넣기");
			}
		}
		/*else {
			sendMessage(token, "죄송합니다. 현재는 배차가 불가능합니다.");
			callInfo.setDeliveryStatus(-1);
			callManagementService.updateCall(callInfo);
		}*/ //기사님이 없을 경우
	
	}
	
	
}

