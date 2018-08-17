package com.example.admin.test1028;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.Proximity2DResult;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;

import static com.esri.android.map.event.OnStatusChangedListener.STATUS.LAYER_LOADED;

/**
 * Adds a layer statically and dynamically and toggles the visibility of top layer
 * with a single tap
 * <p>
 * 代码备份
 * 20180811
 */
public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {


    private MapView map = null;

    //Dynamic layer URL from ArcGIS online
/*	String dynamicMapURL =
			"http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Specialty/ESRI_StateCityHighway_USA/MapServer";
	ArcGISTiledMapServiceLayer basemap;
	ArcGISDynamicMapServiceLayer dynamicLayer;*/
    private GraphicsLayer graphicsLayer;
    //	设定绘制的类型
    private Geometry.Type drawType = null;
    private Symbol symbol = null;
    private SimpleFillSymbol fillSymbol = null;
    //判断是否发生菜单选择事件
    private boolean isChoose;
    private EditText edtDistance;
    private Button btnDistance;
    private Double doubleDistance;
    Point ptEndPoint;//处理后的终点

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the map and initial extent from XML layout
        map = (MapView) findViewById(R.id.map);
        edtDistance = (EditText) findViewById(R.id.edt_distance);
        btnDistance = (Button) findViewById(R.id.btn_distance);
		/*map.addLayer(new ArcGISDynamicMapServiceLayer(
				"http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer"));*/
		/*basemap = new ArcGISTiledMapServiceLayer(this.getResources().getString(
				R.string.basemap_url));
		map.addLayer(basemap);
		*/
		/*dynamicLayer = new ArcGISDynamicMapServiceLayer(this.getResources()
				.getString(R.string.dynamiclayer_url));
		map.addLayer(dynamicLayer);*/
        //Creates a dynamic layer using service URL
	/*	ArcGISDynamicMapServiceLayer dynamicLayer = new ArcGISDynamicMapServiceLayer(dynamicMapURL);
		//Adds layer into the 'MapView'
		map.addLayer(dynamicLayer);*/

        //加载在线切片地图
		/*ArcGISTiledMapServiceLayer tiledMapServiceLayer=new ArcGISTiledMapServiceLayer(
				 "http://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer");
		map.addLayer(tiledMapServiceLayer);*/

        //加载离线切片地图
        ArcGISLocalTiledLayer localtitleLayer = new ArcGISLocalTiledLayer("file:///mnt/sdcard/basemap/东直门.tpk");
        map.addLayer(localtitleLayer);


        //加载在线矢量地图
		/*ArcGISFeatureLayer featureLayer=new ArcGISFeatureLayer("http://192.168.112.112:6080/arcgis/rest"
				+ "/services/Feature/edit_polygon/MapServer/0", MODE.SNAPSHOT);
		map.addLayer(featureLayer);*/

        //设定用户进入许可
		/*UserCredentials creds = new UserCredentials();
		creds.setUserAccount("rick", "rick@esri");

		//加载在线需验证的矢量地图
		ArcGISFeatureLayer secureFeatureLayer = new ArcGISFeatureLayer("https://servicesbeta.esri.com/ArcGIS/rest"
				+ "/services/SanJuan/ColoradoCounties/MapServer/0",
				MODE.SNAPSHOT,creds);
		map.addLayer(secureFeatureLayer);*/
        //setContentView(map);
        map.enableWrapAround(true);
        map.setEsriLogoVisible(true);
        //加载graphiclayer
        graphicsLayer = new GraphicsLayer();
        map.addLayer(graphicsLayer);
        //设定绘制的监听事件，让其能够绘制
        DrawGraphicTouchListener drawgraphictouchlistener = new DrawGraphicTouchListener(this, map);
        map.setOnTouchListener(drawgraphictouchlistener);
        btnDistance.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_distance:
                String strDistance = edtDistance.getText().toString();//获得用户手动输入的长度
                if (strDistance.isEmpty()) {
                    Toast.makeText(MainActivity2.this, "请输入选段长度值!", Toast.LENGTH_SHORT).show();
                } else
                    doubleDistance = Double.parseDouble(strDistance);
                Toast.makeText(MainActivity2.this, "成功获取线段长段值！", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //菜单的加载
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载点选择菜单
        SubMenu pointSubMenu = menu.addSubMenu("点");
        pointSubMenu.setHeaderTitle("选择绘制的点");
        pointSubMenu.add(0, 0, 0, "图点");
        pointSubMenu.add(0, 1, 0, "红点");
        pointSubMenu.add(0, 2, 0, "蓝点");
        pointSubMenu.add(0, 3, 0, "绿点");
        //加载线选择菜单
        SubMenu lineSubMenu = menu.addSubMenu("线");
        lineSubMenu.setHeaderTitle("选择绘制的线");
        lineSubMenu.add(1, 0, 0, "白线");
        lineSubMenu.add(1, 1, 0, "红线");
        lineSubMenu.add(1, 2, 0, "蓝虚线");
        lineSubMenu.add(1, 3, 0, "黄粗线");
        //加载面选择菜单
        SubMenu gonMenu = menu.addSubMenu("面");
        gonMenu.setHeaderTitle("选择绘制的面");
        gonMenu.add(2, 0, 0, "红面");
        gonMenu.add(2, 1, 0, "绿面半透明");
        gonMenu.add(2, 2, 0, "蓝面虚线填充");

        return super.onCreateOptionsMenu(menu);
    }

    //对菜单的点击事件
    //菜单项被单击的事件
    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        isChoose = true;
        //判断是在哪个groupid里面的
        switch (mi.getGroupId()) {
            case 0:
                drawType = Geometry.Type.POINT;
                switch (mi.getItemId()) {
                    case 0:
                        Drawable drawable = this.getResources().getDrawable(R.drawable.ic_launcher_background);
                        symbol = new PictureMarkerSymbol(drawable);
                        break;

                    case 1:
                        symbol = new SimpleMarkerSymbol(Color.RED, 14, SimpleMarkerSymbol.STYLE.CIRCLE);
                        break;

                    case 2:
                        symbol = new SimpleMarkerSymbol(Color.BLUE, 14, SimpleMarkerSymbol.STYLE.CIRCLE);
                        break;

                    case 3:
                        symbol = new SimpleMarkerSymbol(Color.GREEN, 14, SimpleMarkerSymbol.STYLE.CIRCLE);
                        break;

                    default:
                        break;
                }
                break;
            case 1:
                drawType = Geometry.Type.POLYLINE;
                switch (mi.getItemId()) {
                    case 0:
                        symbol = new SimpleLineSymbol(Color.WHITE, 8, SimpleLineSymbol.STYLE.SOLID);
                        break;

                    case 1:
                        symbol = new SimpleLineSymbol(Color.RED, 8, SimpleLineSymbol.STYLE.SOLID);
                        break;

                    case 2:
                        symbol = new SimpleLineSymbol(Color.BLUE, 10, SimpleLineSymbol.STYLE.DASH);
                        break;

                    case 3:
                        symbol = new SimpleLineSymbol(Color.YELLOW, 18, SimpleLineSymbol.STYLE.SOLID);
                        break;

                    default:
                        break;
                }

                break;
            case 2:
                drawType = Geometry.Type.POLYGON;
                switch (mi.getItemId()) {
                    case 0:
                        fillSymbol = new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.SOLID);
                        fillSymbol.setAlpha(100);
                        break;

                    case 1:
                        fillSymbol = new SimpleFillSymbol(Color.GREEN);
                        fillSymbol.setAlpha(50);
                        break;

                    case 2:
                        fillSymbol = new SimpleFillSymbol(Color.BLUE, SimpleFillSymbol.STYLE.BACKWARD_DIAGONAL);
                        fillSymbol.setAlpha(100);
                        break;

                    case 3:

                        break;

                    default:
                        break;
                }

                break;


            default:
                break;
        }


        return false;

    }


    @Override
    protected void onPause() {
        super.onPause();
        map.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.unpause();
    }

    /**
     * @author David.Ocean david_ocean@163.com
     * @ClassName: DrawGraphicTouchListener
     * @Description: 对触碰点击事件进行重写，使得能够进行绘制
     * @date 2013年10月30日 下午2:46:06
     */
    public class DrawGraphicTouchListener extends MapOnTouchListener {
        //		List<Point> pointsList=new ArrayList<Point>();
        Point ptStart = null;
        Point ptPrevious = null;
        Polygon polygon = null;

        public DrawGraphicTouchListener(Context context, MapView view) {
            super(context, view);
        }

        @Override
        public boolean onSingleTap(MotionEvent point) {
            if (isChoose == true) {
                ptPrevious = null;
                ptStart = null;
                polygon = null;
            }
            float x = point.getX();
            float y = point.getY();
            Log.d("point","x: "+x+" , y: "+y);
            Point ptCurrent = map.toMapPoint(x, y);//屏幕坐标转成地理坐标

            Log.d("point","x: "+ptCurrent.getX()+" , y: "+ptCurrent.getY());
            if (drawType == Geometry.Type.POINT) {
                Graphic pGraphic = new Graphic(ptCurrent, symbol);
                graphicsLayer.addGraphic(pGraphic);
            } else {
                if (ptStart == null) {
                    ptStart = ptCurrent;
                    Graphic pgraphic = new Graphic(ptStart, new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE));
                    graphicsLayer.addGraphic(pgraphic);
                } else {
                    Graphic pGraphic = new Graphic(ptCurrent, new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE));
                    graphicsLayer.addGraphic(pGraphic);
                    Line line = new Line();
                    line.setStart(ptPrevious);

                    if (doubleDistance != null) {
                        ptEndPoint = getEndPointByDistance(ptPrevious, ptCurrent, doubleDistance);//最终点
                        line.setEnd(ptEndPoint);//基于客户需求，这个终点需要计算得出-------------2018年08月11日--------------------
                    } else
                        line.setEnd(ptCurrent);
                    if (drawType == Geometry.Type.POLYLINE) {
                        Polyline polyline = new Polyline();
                        polyline.addSegment(line, true);
                        Graphic iGraphic = new Graphic(polyline, symbol);
                        graphicsLayer.addGraphic(iGraphic);
                    } else if (drawType == Geometry.Type.POLYGON) {
                        if (polygon == null) {
                            polygon = new Polygon();
                        }
                        polygon.addSegment(line, false);
                        Graphic gGraphic = new Graphic(polygon, fillSymbol);
                        graphicsLayer.addGraphic(gGraphic);
                    }
                }
            }
            if (ptEndPoint != null) {
                ptPrevious = ptEndPoint;
            } else {
                ptPrevious = ptCurrent;
            }
            isChoose = false;
            return false;

        }

    }

    /**
     * 通过距离形成线段的终点
     *
     * @param fromPoint
     * @param toPoint
     * @param distance
     */
    private Point getEndPointByDistance(Point fromPoint, Point toPoint, double distance) {
        Point endPoint;
        //形成一个待切割的线
        Polyline polyline = new Polyline();
        polyline.startPath(fromPoint);
        polyline.lineTo(toPoint);

        //通过输入的距离形成一个圆
        Geometry bufferedGeo = GeometryEngine.buffer(fromPoint, map.getSpatialReference(), distance, Unit.create(LinearUnit.Code.METER));
        boolean isCross = GeometryEngine.crosses(polyline, bufferedGeo, map.getSpatialReference());
        if (isCross) {       //如果相交，则交点即为要找的endPoint
            Geometry intersectGeo = GeometryEngine.intersect(polyline, bufferedGeo, map.getSpatialReference());
            Polyline intersectLine = (Polyline) intersectGeo;
            endPoint = intersectLine.getPoint(1);
        } else {          //如果不相交，则toPoint距离圆形最近的那个点即为要找的endPoint
            Proximity2DResult nearestCoordinate = GeometryEngine.getNearestCoordinate(bufferedGeo, toPoint, false);
            endPoint = nearestCoordinate.getCoordinate();
        }
        Graphic endPointG = new Graphic(endPoint, symbol);
        graphicsLayer.addGraphic(endPointG);
        return endPoint;
    }
}
