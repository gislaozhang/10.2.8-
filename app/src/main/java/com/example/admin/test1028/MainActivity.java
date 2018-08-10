//package com.example.admin.test1028;
//
//import android.content.Context;
//import android.graphics.Color;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//
//import com.esri.android.map.GraphicsLayer;
//import com.esri.android.map.MapOnTouchListener;
//import com.esri.android.map.MapView;
//
//import com.esri.android.map.ags.ArcGISLocalTiledLayer;
//import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
//
//
//import com.esri.core.geometry.MultiPath;
//import com.esri.core.geometry.Point;
//
//import com.esri.core.geometry.Polyline;
//
//import com.esri.core.map.Graphic;
//import com.esri.core.symbol.LineSymbol;
//import com.esri.core.symbol.SimpleLineSymbol;
//
//
//import java.util.ArrayList;
//
//
//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//    ArrayList<Point> mPoints = new ArrayList<Point>(); //节点
//    Graphic drawGraphic;
//    ArcGISLocalTiledLayer local;
//    GraphicsLayer drawLayer;
//    MapView mapView;
//
//
//    private Button btnDistance, btn_drawline;
//    private EditText editDistance;
//    private DrawTouchListener drawListener;//绘图事件
//    private Polyline polyline;
//    LineSymbol mLineSymbol;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        InitData();//初始化数据
//    }
//
//    public void InitData() {
//        mapView = (MapView) findViewById(R.id.map);
//        btnDistance = (Button) findViewById(R.id.btn_distance);
//        btn_drawline = (Button) findViewById(R.id.btn_drawline);
//        editDistance = (EditText) findViewById(R.id.edt_distance);
//        String strMapUrl = "http://map.geoq.cn/ArcGIS/rest/services/ChinaOnlineCommunity/MapServer";
//
//        ArcGISTiledMapServiceLayer serviceLayer = new ArcGISTiledMapServiceLayer(strMapUrl);
//        mapView.addLayer(serviceLayer);//添加图层到地图窗口中
//
//        mLineSymbol = new SimpleLineSymbol(Color.BLACK, 2);
//
//        btnDistance.setOnClickListener(this);// “获取长度”按钮监听事件
//        btn_drawline.setOnClickListener(this);// “获取长度”按钮监听事件
//
//
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_distance:// 单击获取长度按钮
//                String strDistance = editDistance.getText().toString();// 获取长度文本框的内容
//                Toast.makeText(MainActivity.this,"click distance button...",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.btn_drawline:// 单击绘制线按钮
//                drawListener = new DrawTouchListener(MainActivity.this,
//                        mapView);
//                this.polyline = new Polyline();
//
//                mapView.setOnTouchListener(drawListener);
//                mapView.addLayer(drawLayer);
//
//                break;
//        }
//    }
//
//    /**
//     * 扩展MapOnTouchListener，实现画图功能
//     */
//    class DrawTouchListener extends MapOnTouchListener {
//
//        public DrawTouchListener(Context context, MapView view) {
//            super(context, view);
//        }
//
//        @Override
//        public boolean onTouch(View view, MotionEvent event) {
//
//            //检查是否为空
//            if (event == null) return false;
//            Point point = mapView.toMapPoint(event.getX(), event.getY());
//
//            polyline.startPath(point);
//            return true;
//
//        }
//
//
//        @Override
//        public boolean onSingleTap(MotionEvent event) {
//            //toMapPoint：屏幕坐标转空间坐标
//            //A convenience method that will convert a device's screen coordinates into an ArcGIS geometry Point
//            // that has the same spatial coordinate system as the MapView's.
//            Point point = mapView.toMapPoint(event.getX(), event.getY());
//
//            //添加点到集合中：ArrayList<Point> mPoints = new ArrayList<Point>(); //节点
//            mPoints.add(point);
//
//            drawPolyline();//绘制线
//
//            return super.onSingleTap(event);
//        }
//    }
//
//    /**
//     * 通过mPoints中的顶点和输入的长度绘制折线
//     */
//    private void drawPolyline() {
//        MultiPath multipath;
//        if (mPoints.size() >= 1) {
//            //利用节点信息创建MultiPath信息
//
//            multipath = new Polyline();
//
//            multipath.startPath(mPoints.get(0));//基于一个点开始一条路径
//            for (int i = 1; i < mPoints.size(); i++) {
//                multipath.lineTo(mPoints.get(i));//点追加到线
//            }
//            //创建多段线
//            polyline = (Polyline) multipath;//保存线数据到全局变量
//           drawGraphic = new Graphic(polyline, mLineSymbol);
//
//        drawLayer = new GraphicsLayer();
//
//           drawLayer.addGraphic(drawGraphic);
//
//        }
//    }
//
//    /**
//     * 通过距离形成线段的终点
//     *
//     * @param fromPoint 起点
//     * @param toPoint   终点
//     * @param distance  距离
//     */
////    private Point getEndPointByDistance(Point fromPoint, Point toPoint, double distance) {
////        Point endPoint;
////        //形成一个待切割的线
////        Polyline polyline = new Polyline();
////        polyline.startPath(fromPoint);
////        polyline.lineTo(toPoint);
////
////        //通过输入的距离形成一个圆
////        Geometry bufferedGeo = GeometryEngine.buffer(fromPoint, mapView.getSpatialReference(), distance, Unit.create(LinearUnit.Code.METER));
////        boolean isCross = GeometryEngine.crosses(polyline, bufferedGeo, mapView.getSpatialReference());
////        if (isCross) {       //如果相交，则交点即为要找的endPoint
////            Geometry intersectGeo = GeometryEngine.intersect(polyline, bufferedGeo, mapView.getSpatialReference());
////            Polyline intersectLine = (Polyline) intersectGeo;
////            endPoint = intersectLine.getPoint(1);
////        } else {          //如果不想交，则toPoint距离圆形最近的那个点即为要找的endPoint
////            Proximity2DResult nearestCoordinate = GeometryEngine.getNearestCoordinate(bufferedGeo, toPoint, false);
////            endPoint = nearestCoordinate.getCoordinate();
////        }
////        Graphic endPointG = new Graphic(endPoint, pointMS);
////        MainActivity.drawLayer.addGraphic(endPointG);
////        return endPoint;
////    }
//
//    /**
//     * 从sdcard根目录获得离线缓存数据
//     */
////    private void getTileFromSdcard() {
////        boolean isSdCardExist = Environment.getExternalStorageState().equals(
////                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
////        if (isSdCardExist) {
////            String sdpath = Environment.getExternalStorageDirectory()
////                    .getAbsolutePath();// 获取sdcard的根路径
////            Log.d("path", sdpath.toString());
////            String filepath = sdpath + File.separator + "Layers";
////            Log.d("path", "moxinglujing" + filepath.toString());
////            if (!filepath.isEmpty()) {
////                local = new ArcGISLocalTiledLayer(filepath);
////                mMapView.addLayer(local);
////            }
////        } else {
////            Toast.makeText(MainActivity.this, "sdcard is not exit", Toast.LENGTH_SHORT).show();
////        }
////    }
////
////    /**
////     * 从sdcard根目录获得tpk数据
////     */
////    private void getTpkFromSdcard() {
////        boolean isSdCardExist = Environment.getExternalStorageState().equals(
////                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
////        if (isSdCardExist) {
////            String sdpath = Environment.getExternalStorageDirectory()
////                    .getAbsolutePath();// 获取sdcard的根路径
////            Log.d("path", sdpath.toString());
////            String filepath = sdpath + File.separator + "beijing.tpk";
////            Log.d("path", "moxinglujing" + filepath.toString());
////            if (!filepath.isEmpty()) {
////                local = new ArcGISLocalTiledLayer(filepath);
////                mMapView.addLayer(local);
////            }
////        } else {
////            Toast.makeText(MainActivity.this, "sdcard is not exit", Toast.LENGTH_SHORT).show();
////        }
////    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.pause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.unpause();
//    }
//
//}