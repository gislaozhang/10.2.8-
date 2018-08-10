package com.example.admin.test1028;

/* Copyright 2012 ESRI
 *
 * All rights reserved under the copyright laws of the United States
 * and applicable international laws, treaties, and conventions.
 *
 * You may freely redistribute and use this sample code, with or
 * without modification, provided you include the original copyright
 * notice and use restrictions.
 *
 * See the Sample code usage restrictions document for further information.
 *
 */


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;

/**
 * Adds a layer statically and dynamically and toggles the visibility of top layer
 * with a single tap
 */
public class MainActivity1 extends AppCompatActivity {


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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the map and initial extent from XML layout
        map = (MapView) findViewById(R.id.map);
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
            Point ptCurrent = map.toMapPoint(x, y);
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
            ptPrevious = ptCurrent;
            isChoose = false;
            return false;

        }

    }

}
