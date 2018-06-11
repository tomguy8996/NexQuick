package com.balbadak.nexquickpro;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.annotation.SuppressLint;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;


@SuppressLint("ValidFragment")
public class fragment_route extends Fragment {

    Context context;
    ViewPager viewPager;
    TMapView tMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_route, container, false);
        context = this.getActivity();
        Button cancelBtn = (Button) view.findViewById(R.id.quick_cancel);
        Button phoneBtn = (Button) view.findViewById(R.id.quick_phone);
        Button finishBtn = (Button) view.findViewById(R.id.quick_finish);
        viewPager = getActivity().findViewById(R.id.pager);
        LinearLayout linearLayoutTmap = (LinearLayout) view.findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(context);

        tMapView.setSKTMapApiKey( "2c831aee-8c6e-444b-82ed-1a23b76e504c" );
        linearLayoutTmap.addView( tMapView );

// 마커 아이콘
        Bitmap qpMark = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.scooter), 100, 100, true);
        Bitmap sender = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.location), 100, 100, true);
        Bitmap receiver = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.location_primary), 100, 100, true);

        TMapMarkerItem qpMarker = new TMapMarkerItem();
        TMapPoint qpPoint = new TMapPoint(37.570841, 126.985302); // QP 위치
        qpMarker.setIcon(qpMark); // 마커 아이콘 지정
        qpMarker.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        qpMarker.setTMapPoint( qpPoint ); // 마커의 좌표 지정
        qpMarker.setName("Pro님의 현재 위치"); // 마커의 타이틀 지정
        tMapView.addMarkerItem("qpMarker", qpMarker); // 지도에 마커 추가
        tMapView.setCenterPoint( qpPoint.getLongitude(), qpPoint.getLatitude() );

        TMapPoint tMapPointStart = qpPoint; // 출발지

        ArrayList<TMapPoint> passList = new ArrayList<>();
        TMapMarkerItem newMarker;
        TMapPoint newPoint;
        for (int i=0; i<10; i++){
            newMarker = new TMapMarkerItem();
            newPoint = new TMapPoint(37.56990, 126.98227);
            newMarker.setIcon(receiver); // 마커 아이콘 지정
            newMarker.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
            newMarker.setTMapPoint( newPoint ); // 마커의 좌표 지정
            newMarker.setName((i+1)+"번째 방문 위치"); // 마커의 타이틀 지정
            tMapView.addMarkerItem("Point"+(i+1), newMarker); // 지도에 마커 추가
            tMapView.setCenterPoint( newPoint.getLongitude(), newPoint.getLatitude() );
            passList.add(newPoint);
        }

        NetworkTask networkTask = new NetworkTask(qpPoint, passList.get(passList.size()-1), passList);
        networkTask.execute();


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }

    public class NetworkTask extends AsyncTask<Void, Void, TMapPolyLine> {

        private TMapPoint tMapPointStart;
        private TMapPoint tMapPointEnd;
        private ArrayList<TMapPoint> passList;

        public NetworkTask(TMapPoint tMapPointStart, TMapPoint tMapPointEnd, ArrayList<TMapPoint> passList) {

            this.tMapPointStart = tMapPointStart;
            this.tMapPointEnd = tMapPointEnd;
            this.passList = passList;
        }

        @Override
        protected TMapPolyLine doInBackground(Void... params) {

            TMapPolyLine tMapPolyLine = null;
            try {
                tMapPolyLine = new TMapData().findMultiPointPathData( tMapPointStart, tMapPointEnd, passList, 0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            return tMapPolyLine;
        }

        @Override
        protected void onPostExecute(TMapPolyLine tMapPolyLine) {
            super.onPostExecute(tMapPolyLine);

            tMapPolyLine.setLineColor(Color.BLUE);
            tMapPolyLine.setLineWidth(2);
            tMapView.addTMapPolyLine("Line1", tMapPolyLine);
        }
    }
}
