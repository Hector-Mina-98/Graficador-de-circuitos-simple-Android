package com.heccoder.hfmq.graficadordecircuitossimple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Workspace extends AppCompatActivity {

    //////////////////////////////////////////////vistas//////////////////////////////////////////////////
    TextView tView, tView2, tView3;
    TextView tView4;
    ConstraintLayout constraint_canvas;
    LinearLayout linear_activity;

    ArrayList<Figura> figuras = new ArrayList<>();    Figura imgaux, imgauxFin;
    ArrayList<ImageView> images_panel = new ArrayList<>();    ImageView img_cable;
    ArrayList<Cable> cables = new ArrayList<>();
    ArrayList<ImageView> cablesSecundarios = new ArrayList<>();

    Cuadricula cuadricula;
    LineaVertical lineaVertical;
    LineaHorizontal lineaHorizontal;

    /////////////////////////////////////////////////////////////////////////////////////////////////////

    int xref,yref,   x,y,    a_canvas,b_canvas;
    int[] location_canvas = new int[2];
    float a_canvas2, b_canvas2;
    int visible_x_imgaux, visible_y_imgaux;
    int moveimgx, moveimgy;
    int b;
    Timer T;    int periodo;    int tol;
    int count;
    MotionEvent me;

    Display display;  int width_disp, height_disp;
    Point size;
    //////////////////////////////////////////variables de gestos zoom/////////////////////////////////////////////
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    boolean scaleOngoing;
    boolean allowmove;

    ////////////////////////////////////animacion de desplazamiento automatico//////////////////////////////////////////////
    Timer TanimMOB;     float v_animMOB;    float incremento_animMOB;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    Button recenter;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    LinearLayout linear_panel_child_A;
    ConstraintLayout constraint_panel;
    int symb_select;
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    int canvas_width, canvas_height;
    boolean add;

    ///////////////////////////////////////cable/////////////////////////////////////////////////////////////////////////////
    int moveimgx_ref, moveimgy_ref, x_cable, y_cable;
    ImageView cable, cable2, cable3;
    String orientacion;
    float grosor_cable;
    int terminal;
    int idCable;

    int idCX;

    int idFigura;

    int termRelOrigen, termRelFin;   //terminales relativos
    String secuencia_cable;



    int seleccionFoco;
    int id_imgauxtemp;

    Context contexto;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace);
        getSupportActionBar().hide();
        contexto = this;

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        width_disp = size.x;
        height_disp = size.y;

        tView = (TextView) findViewById(R.id.textView1);
        tView2 = (TextView) findViewById(R.id.textView2);
        tView3 = (TextView) findViewById(R.id.tView3);
        constraint_canvas = (ConstraintLayout) findViewById(R.id.constraint_canvas);
        linear_activity = (LinearLayout) findViewById(R.id.linear_activity);
        linear_panel_child_A = findViewById(R.id.linear_panel_child_A);
        constraint_panel = findViewById(R.id.constraint_panel);

        imgaux = new Figura(this, R.drawable.cable, idFigura);
        constraint_canvas.addView(imgaux);
        imgaux.setVisibility(View.GONE);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        images_panel.add((ImageView) findViewById(R.id.img_fv));
        images_panel.add((ImageView) findViewById(R.id.img_fvc));
        images_panel.add((ImageView) findViewById(R.id.img_fi));
        images_panel.add((ImageView) findViewById(R.id.img_fic));
        images_panel.add((ImageView) findViewById(R.id.img_fvac));
        images_panel.add((ImageView) findViewById(R.id.img_resistencia));
        images_panel.add((ImageView) findViewById(R.id.img_capacitancia));
        images_panel.add((ImageView) findViewById(R.id.img_inductancia));
        images_panel.add((ImageView) findViewById(R.id.img_tierra));
        images_panel.add((ImageView) findViewById(R.id.img_cable));
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        a_canvas2 = 1.0f;
        b_canvas2 = 1.0f;
        allowmove = true;
        periodo = 300;  tol = 3;
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        recenter = findViewById(R.id.recenter);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        symb_select = -1;
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        canvas_width = constraint_canvas.getLayoutParams().width;
        canvas_height = constraint_canvas.getLayoutParams().height;
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        add = false;

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        cuadricula = new Cuadricula(this);
        constraint_canvas.addView(cuadricula);
        lineaVertical = new LineaVertical(this);
        constraint_canvas.addView(lineaVertical);
        lineaHorizontal = new LineaHorizontal(this);
        constraint_canvas.addView(lineaHorizontal);

        lineaHorizontal.setVisibility(View.INVISIBLE);
        lineaVertical.setVisibility(View.INVISIBLE);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        grosor_cable = 4.0F;

        cable = new ImageView(this);
        cable.setImageResource(R.drawable.cable);
        constraint_canvas.addView(cable);
        cable.setLayoutParams(new ConstraintLayout.LayoutParams((int)grosor_cable,(int)grosor_cable));

        cable2 = new ImageView(this);
        cable2.setImageResource(R.drawable.cable);
        constraint_canvas.addView(cable2);
        cable2.setLayoutParams(new ConstraintLayout.LayoutParams((int)grosor_cable,(int)grosor_cable));

        cable3 = new ImageView(this);
        cable3.setImageResource(R.drawable.cable);
        constraint_canvas.addView(cable3);
        cable3.setLayoutParams(new ConstraintLayout.LayoutParams((int)grosor_cable,(int)grosor_cable));

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            public void onScaleEnd(ScaleGestureDetector detector) {
                scaleOngoing = false;
            }
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                scaleOngoing = true;
                return true;
            }
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                // do scaling here
                mScaleFactor *= scaleGestureDetector.getScaleFactor();
                mScaleFactor = Math.max(0.5f,
                        Math.min(mScaleFactor, 3.0f));
                constraint_canvas.setScaleX(mScaleFactor);
                constraint_canvas.setScaleY(mScaleFactor);
                a_canvas2 = mScaleFactor;
                b_canvas2 = mScaleFactor;
                //tView.setText("ScalePaper  aW: " + a_canvas2 + "  bH: " + b_canvas2);

                return true;
                //return false;
            }
        });

        recenter(null);

        oyente();


        //////////////////////////////////////////////////////////////////////////////////
        cuadrito = new ImageView(this);
        cuadrito.setImageResource(R.drawable.cable);
        constraint_canvas.addView(cuadrito);
        cuadrito.getLayoutParams().width = 30;
        cuadrito.getLayoutParams().height = 30;
        cuadrito.setImageAlpha(0);

        construyendo_cable = false;
        idCable = 1000;
        idFigura = 5000;
        idCX = 10000;

        tView4 = findViewById(R.id.tView4);

        seleccionFoco = 0;

    }

    ImageView cuadrito;
    boolean construyendo_cable;


    public int diferenciaX(int a){
        constraint_canvas.setX(a);
        constraint_canvas.getLocationOnScreen(location_canvas);
        int a2 = location_canvas[0];
        return a2-a;
    }
    public int diferenciaY(int b){
        constraint_canvas.setY(b);
        constraint_canvas.getLocationOnScreen(location_canvas);
        int b2 = location_canvas[1];
        return b2-b;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void oyente(){
        linear_activity.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mScaleGestureDetector.onTouchEvent(motionEvent);
                me = motionEvent;

                int action = motionEvent.getActionMasked();
                switch (action) {

                    case MotionEvent.ACTION_DOWN:

                        xref = (int) motionEvent.getX();
                        yref = (int) motionEvent.getY();

                        constraint_canvas.getLocationOnScreen(location_canvas);
                        a_canvas = location_canvas[0];
                        b_canvas = location_canvas[1];

                        oyenteFigura();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        constraint_canvas.getLocationOnScreen(location_canvas);
                        tView.setText("C1:" + location_canvas[0] + "  C2:" + location_canvas[1]);

                        if(scaleOngoing){
                            allowmove = false;
                        }

                        if(allowmove){
                            int difx = diferenciaX(a_canvas);
                            int dify = diferenciaY(b_canvas);
                            x = (int) motionEvent.getX();
                            y = (int) motionEvent.getY();
                            constraint_canvas.setX((x - xref)+(a_canvas-difx));
                            constraint_canvas.setY((y - yref)+(b_canvas-dify));
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        b = 0;
                        allowmove = true;

                        x = (int) motionEvent.getX();
                        y = (int) motionEvent.getY();

                        constraint_canvas.setOnTouchListener(new View.OnTouchListener() {
                            public boolean onTouch(View view, MotionEvent motionEvent) {

                                int action = motionEvent.getActionMasked();
                                switch (action) {

                                    case MotionEvent.ACTION_DOWN:
                                        add = true;
                                        break;
                                }
                                return false;
                            }
                        });

                        if((Math.abs(x-xref)<10) && (Math.abs(y-yref)<10) && symb_select >= 0 && add){
                            if(symb_select != 9) add();
                            oyenteFigura();
                        }

                        if((Math.abs(x-xref)<10) && (Math.abs(y-yref)<10) && imgaux != null){
                            senalDeFoco(false);
                        }

                        add = false;
                        break;
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void oyenteFigura () {
        for(b = 0; b < figuras.size(); b++){
            figuras.get(b).setOnTouchListener(new View.OnTouchListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                public boolean onTouch(View view, MotionEvent motionEvent2) {
                    tView2.setText("Adentro for" + b);
                    imgaux = (Figura) view;
                    imgaux.setId(view.getId());
                    MotionEvent motionEvent = me;
                    int action = motionEvent.getActionMasked();
                    switch (action) {

                        case MotionEvent.ACTION_DOWN:
                            recenter.setVisibility(View.INVISIBLE);

                            xref = (int) motionEvent.getX();
                            yref = (int) motionEvent.getY();

                            constraint_canvas.getLocationOnScreen(location_canvas);
                            a_canvas = location_canvas[0];
                            b_canvas = location_canvas[1];
                            count = 0;
                            T = new Timer();
                            T.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            count++;
                                            tView.setText("" + count);
                                            if(count > 1){
                                                if(symb_select != 9){
                                                    lineaHorizontal.setVisibility(View.VISIBLE);
                                                    lineaVertical.setVisibility(View.VISIBLE);
                                                }
                                                lineaVertical.setX(visible_x_imgaux);
                                                lineaHorizontal.setY(visible_y_imgaux);
                                            }
                                            imgaux.setImageAlpha(70);
                                        }
                                    });
                                }
                            }, 1, periodo);

                            if(symb_select == 9){

                                moveimgx_ref = moveimgx_ref-(moveimgx_ref%30);
                                moveimgy_ref = moveimgy_ref-(moveimgy_ref%30);

                                if(imgaux.getAngulo() == 0 || imgaux.getAngulo() == 180){
                                    orientacion = "vertical";
                                    if(imgaux.getAngulo() == 0){
                                        if(motionEvent2.getY() < 60) {
                                            terminal = 1;
                                            moveimgy_ref = (int)(imgaux.getY());
                                        }
                                        else {
                                            terminal = 2;
                                            moveimgy_ref = (int)(imgaux.getY()+120);
                                        }
                                    }else if(imgaux.getAngulo() == 180){
                                        if(motionEvent2.getY() > 60) {
                                            terminal = 2;
                                            moveimgy_ref = (int)(imgaux.getY());
                                        }
                                        else {
                                            terminal = 1;
                                            moveimgy_ref = (int)(imgaux.getY()+120);
                                        }
                                    }
                                    moveimgx_ref = (int)(imgaux.getX()+30);

                                    cable3.setScaleY(0);
                                    cable3.setX(moveimgx_ref);
                                }else{
                                    //tView2.setText("" + motionEvent2.getX());
                                    orientacion = "horizontal";
                                    if(imgaux.getAngulo() == 90){
                                        if(motionEvent2.getY() < 60) {
                                            terminal = 1;
                                            moveimgx_ref = (int)(imgaux.getX()+90);
                                        }
                                        else {
                                            terminal = 2;
                                            moveimgx_ref = (int)(imgaux.getX()-30);
                                        }
                                    }else if(imgaux.getAngulo() == 270){
                                        if(motionEvent2.getY() > 60) {
                                            terminal = 2;
                                            moveimgx_ref = (int)(imgaux.getX()+90);
                                        }
                                        else {
                                            terminal = 1;
                                            moveimgx_ref = (int)(imgaux.getX()-30);
                                        }
                                    }
                                    moveimgy_ref = (int)(imgaux.getY()+60);

                                    cable3.setScaleY(0);
                                    cable3.setX(moveimgx_ref);
                                }

                                moveimgx_ref = (int) (moveimgx_ref - (grosor_cable/2));
                                moveimgy_ref = (int) (moveimgy_ref - (grosor_cable/2));
                            }

                            break;

                        case MotionEvent.ACTION_MOVE:

                            x = (int) motionEvent.getX();
                            y = (int) motionEvent.getY();

                            moveimgx = (int)((x-a_canvas-(50*a_canvas2))/a_canvas2);
                            moveimgy = (int)((y-b_canvas-(40*b_canvas2))/b_canvas2);

                            if(symb_select != 9){

                                if(count > 1){
                                    eliminarCable(imgaux);
                                    constraint_canvas.getLocationOnScreen(location_canvas);
                                    a_canvas = location_canvas[0];
                                    b_canvas = location_canvas[1];
                                    if(adentro()!=1 && adentro()!=12){
                                        imgaux.setX(moveimgx-(moveimgx%30));
                                        visible_x_imgaux = (int)(moveimgx-(moveimgx%30)-32+30);
                                        visible_x_imgaux = visible_x_imgaux - 30;
                                    }
                                    if(adentro()!=2 && adentro()!=12){
                                        imgaux.setY(moveimgy-(moveimgy%30F));
                                        visible_y_imgaux = (int)(moveimgy-(moveimgy%30)-32+30);
                                    }
                                    lineaVertical.setX(visible_x_imgaux);
                                    lineaHorizontal.setY(visible_y_imgaux);
                                    count = 2;
                                    //tView.setText("X:" + x + "  Y:" + y);
                                    tView.setText("W:" + width_disp + "  H:" + height_disp);

                                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    if(x<(width_disp*0.3) && y>(height_disp-height_disp*0.2)) {
                                        tView2.setText("<-- v");
                                        if(TanimMOB!=null){
                                            TanimMOB.cancel();
                                            TanimMOB = null;
                                        }
                                    }else if(x>(width_disp-width_disp*0.2) && y<(height_disp*0.2)) {
                                        tView2.setText("^l -->");
                                        if(TanimMOB!=null){
                                            TanimMOB.cancel();
                                            TanimMOB = null;
                                        }
                                        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                    }else if(x<(width_disp*0.3) && y>(height_disp*0.2)){
                                        tView2.setText("<--");
                                        if(adentro()!=1 && adentro()!=12){
                                            if(TanimMOB == null) animMOB(constraint_canvas, "TranslationX", location_canvas[0]+(a_canvas2*1.0F-1.0F)*600.0F, (location_canvas[0]+(a_canvas2*1.0F-1.0F)*300.0F)+1000, 4000);
                                        }
                                    }else if(x>(width_disp*0.2) && y<(height_disp*0.2)){
                                        tView2.setText("^l");
                                        if(adentro()!=2 && adentro()!=12){
                                            if(TanimMOB == null) animMOB(constraint_canvas, "TranslationY", location_canvas[1]+(a_canvas2*1.0F-1.0F)*390.0F, (location_canvas[1]+(a_canvas2*1.0F-1.0F)*300.0F)+1000, 4000);
                                        }
                                    }else if(x<(width_disp*0.3) && y<(height_disp*0.2)){
                                        tView2.setText("<-- ^l");
                                        if(TanimMOB!=null){
                                            TanimMOB.cancel();
                                            TanimMOB = null;
                                        }

                                        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    }else if(x>(width_disp-width_disp*0.2) && y>(height_disp-height_disp*0.2)) {
                                        tView2.setText("--> v");
                                        if(TanimMOB!=null){
                                            TanimMOB.cancel();
                                            TanimMOB = null;
                                        }
                                    }else if(x>(width_disp-width_disp*0.2) && y<(height_disp-height_disp*0.2)) {
                                        tView2.setText("-->");
                                        if (adentro()!=1 && adentro()!=12) {
                                            if(TanimMOB == null) animMOB(constraint_canvas, "TranslationX", location_canvas[0]+(a_canvas2*1.0F-1.0F)*600.0F, (location_canvas[0]+(a_canvas2*1.0F-1.0F)*300.0F)-1000,4000);
                                        }
                                    }else if(x<(width_disp-width_disp*0.2) && y>(height_disp-height_disp*0.2)) {
                                        tView2.setText("v");
                                        if (adentro()!=2 && adentro()!=12) {
                                            if(TanimMOB == null) animMOB(constraint_canvas, "TranslationY", location_canvas[1]+(a_canvas2*1.0F-1.0F)*390.0F, (location_canvas[1]+(a_canvas2*1.0F-1.0F)*300.0F)-1000,4000);
                                        }

                                    }else{

                                        if(TanimMOB!=null){
                                            TanimMOB.cancel();
                                            TanimMOB = null;
                                        }
                                        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    }

                                }else {
                                    if(scaleOngoing){
                                        allowmove = false;
                                    }
                                    if(allowmove){
                                        int difx = diferenciaX(a_canvas);
                                        int dify = diferenciaY(b_canvas);
                                        x = (int) motionEvent.getX();
                                        y = (int) motionEvent.getY();
                                        constraint_canvas.setX((x - xref)+(a_canvas-difx));
                                        constraint_canvas.setY((y - yref)+(b_canvas-dify));
                                    }
                                    if((x-xref)>tol || (xref-x)>tol || (y-yref)>tol || (yref-y)>tol) count = 0;
                                    tView.setText("" + count);
                                }

                            }else{
                                //construyendo_cable = true;

                                x_cable = (int)((x-a_canvas-(a_canvas2))/a_canvas2);
                                y_cable = (int)((y-b_canvas-(b_canvas2))/b_canvas2);

                                x_cable = (x_cable-(x_cable%30));
                                y_cable = (y_cable-(y_cable%30));

                                construyendo_cable = dibujarCable(orientacion,moveimgx_ref,moveimgy_ref,x_cable,y_cable);

                            }

                            senalDeFoco(true);

                            break;

                        case MotionEvent.ACTION_UP:
                            x = (int) motionEvent.getX();
                            y = (int) motionEvent.getY();

                            imgaux.setImageAlpha(255);

                            if((Math.abs(x-xref)<10) && (Math.abs(y-yref)<10)){
                                senalDeFoco(false);
                            }

                            recenter.setVisibility(View.VISIBLE);

                            b = 0;
                            allowmove = true;
                            T.cancel();
                            count = 0;

                            lineaHorizontal.setVisibility(View.INVISIBLE);
                            lineaVertical.setVisibility(View.INVISIBLE);

                            if(TanimMOB!=null){
                                TanimMOB.cancel();
                                TanimMOB = null;
                            }

                            if(construyendo_cable) {
                                Cable c1 = new Cable(imgaux, imgauxFin, idCable++, cable, cable2, cable3);
                                cables.add(c1);
                                construirCable(c1.getIdCable());
                            }
                            imgauxFin = null;

                            construyendo_cable = false;
                            //cuadrito.setX(imgaux.getX());
                            //cuadrito.setY(imgaux.getY());
                            cable.setX(0);
                            cable.setY(0);
                            cable.setScaleX(0);
                            cable2.setX(0);
                            cable2.setY(0);
                            cable2.setScaleY(0);
                            cable3.setX(0);
                            cable3.setY(0);
                            cable3.setScaleX(0);
                            cable3.setScaleY(0);

                            break;
                    }

                    return true;
                }
            });
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public void oyenteCable () {

        for (int c = 0; c < figuras.size(); c++) {
            figuras.get(c).setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View view, MotionEvent motionEvent) {

                    int action = motionEvent.getActionMasked();
                    switch (action) {

                        case MotionEvent.ACTION_DOWN:

                            break;

                    }

                    return true;

                }
            });
        }

    }


    public void senalDeFoco (boolean move) {

        for(int sdf = 0; sdf < figuras.size(); sdf++) {
            figuras.get(sdf).setBackgroundColor(Color.TRANSPARENT);
        }

        if(!move) {
            if (seleccionFoco == 1 && imgaux.getID() == id_imgauxtemp) seleccionFoco = 0;
            else {
                seleccionFoco = 1;
            }

        } else {
            seleccionFoco = 1;
        }

        if (seleccionFoco == 1 && imgaux != null) {
            imgaux.setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
            id_imgauxtemp = imgaux.getID();
        } else {
            imgaux = null;
            id_imgauxtemp = 0;
        }

    }


    public int adentro () {

        if (moveimgx <= 0 && (moveimgy >= (canvas_height-(canvas_height%30))-90) ){     // <-- v
            if(TanimMOB!=null){
                TanimMOB.cancel();
                TanimMOB = null;
            }
            return 12;
        }

        if ((moveimgx >= (canvas_width-(canvas_width%30))-30) && moveimgy <= 0 ){     // --> |^
            if(TanimMOB!=null){
                TanimMOB.cancel();
                TanimMOB = null;
            }
            return 12;
        }

        if (moveimgx <= 0 && moveimgy <= 0 ){       // <-- |^
            if(TanimMOB!=null){
                TanimMOB.cancel();
                TanimMOB = null;
            }
            return 12;
        }

        if ((moveimgx >= (canvas_width-(canvas_width%30))-30) && (moveimgy >= (canvas_height-(canvas_height%30))-90) ){     // --> v
            if(TanimMOB!=null){
                TanimMOB.cancel();
                TanimMOB = null;
            }
            return 12;
        }

        if (moveimgx <= 0){     // <--
            if(TanimMOB!=null){
                TanimMOB.cancel();
                TanimMOB = null;
            }
            return 1;
        }

        if (moveimgy <= 0 ){    // |^
            if(TanimMOB!=null){
                TanimMOB.cancel();
                TanimMOB = null;
            }
            return 2;
        }

        if (moveimgx >= (canvas_width-(canvas_width%30))-30){       // -->
            if(TanimMOB!=null){
                TanimMOB.cancel();
                TanimMOB = null;
            }
            return 1;
        }

        if (moveimgy >= (canvas_height-(canvas_height%30))-90){     // v
            if(TanimMOB!=null){
                TanimMOB.cancel();
                TanimMOB = null;
            }
            return 2;
        }
        //////////////////////////////////

        return 0;

    }



    public void add () {
        Figura nueva_fig = new Figura(this, R.drawable.cable, idFigura++);
        switch (symb_select) {
            case 0:
                nueva_fig.setImageResource(R.drawable.fv);
                break;
            case 1:
                nueva_fig.setImageResource(R.drawable.fv_controlada);
                break;
            case 2:
                nueva_fig.setImageResource(R.drawable.fi);
                break;
            case 3:
                nueva_fig.setImageResource(R.drawable.fi_controlada);
                break;
            case 4:
                nueva_fig.setImageResource(R.drawable.fv_ac);
                break;
            case 5:
                nueva_fig.setImageResource(R.drawable.resistencia);
                break;
            case 6:
                nueva_fig.setImageResource(R.drawable.capacitancia);
                break;
            case 7:
                nueva_fig.setImageResource(R.drawable.inductancia);
                break;
            case 8:
                nueva_fig.setImageResource(R.drawable.tierra);
                break;
        }
        constraint_canvas.addView(nueva_fig);
        if(symb_select != 8){
            nueva_fig.getLayoutParams().width = 60;
            nueva_fig.getLayoutParams().height = 120;
        }else {
            nueva_fig.getLayoutParams().width = 60;
            nueva_fig.getLayoutParams().height = 60;
        }
        nueva_fig.setScaleX(1.07F);
        nueva_fig.setScaleY(1.07F);

        moveimgx = ((int)((x-a_canvas-(50*a_canvas2))/a_canvas2));
        moveimgy = ((int)((y-b_canvas-(40*b_canvas2))/b_canvas2));
        nueva_fig.setX(moveimgx-(moveimgx%30));
        nueva_fig.setY(moveimgy-(moveimgy%30));
        figuras.add(nueva_fig);
        tView3.setText("Figuras: " + (figuras.size()));
    }



    public void addFv (View vista) {
        if (symb_select == 0) {
            symb_select = -1;
            for (int d = 0; d < images_panel.size(); d++) {
                images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
            }
        }else {
            for (int d = 0; d < images_panel.size(); d++) {
                if (d == 0) {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.add));
                    symb_select = d;
                }else {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
                }
            }
        }
    }


    public void addFvc (View vista) {
        if (symb_select == 1) {
            symb_select = -1;
            for (int d = 0; d < images_panel.size(); d++) {
                images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
            }
        }else {
            for (int d = 0; d < images_panel.size(); d++) {
                if (d == 1) {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.add));
                    symb_select = d;
                }else {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
                }
            }
        }
    }


    public void addFi (View vista) {
        if (symb_select == 2) {
            symb_select = -1;
            for (int d = 0; d < images_panel.size(); d++) {
                images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
            }
        }else {
            for (int d = 0; d < images_panel.size(); d++) {
                if (d == 2) {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.add));
                    symb_select = d;
                }else {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
                }
            }
        }
    }


    public void addFic (View vista) {
        if (symb_select == 3) {
            symb_select = -1;
            for (int d = 0; d < images_panel.size(); d++) {
                images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
            }
        }else {
            for (int d = 0; d < images_panel.size(); d++) {
                if (d == 3) {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.add));
                    symb_select = d;
                }else {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
                }
            }
        }
    }


    public void addFvac (View vista) {
        if (symb_select == 4) {
            symb_select = -1;
            for (int d = 0; d < images_panel.size(); d++) {
                images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
            }
        }else {
            for (int d = 0; d < images_panel.size(); d++) {
                if (d == 4) {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.add));
                    symb_select = d;
                }else {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
                }
            }
        }
    }


    public void addResistencia (View vista) {
        if (symb_select == 5) {
            symb_select = -1;
            for (int d = 0; d < images_panel.size(); d++) {
                images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
            }
        }else {
            for (int d = 0; d < images_panel.size(); d++) {
                if (d == 5) {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.add));
                    symb_select = d;
                }else {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
                }
            }
        }
    }


    public void addCapacitancia (View vista) {
        if (symb_select == 6) {
            symb_select = -1;
            for (int d = 0; d < images_panel.size(); d++) {
                images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
            }
        }else {
            for (int d = 0; d < images_panel.size(); d++) {
                if (d == 6) {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.add));
                    symb_select = d;
                }else {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
                }
            }
        }
    }


    public void addInductancia (View vista) {
        if (symb_select == 7) {
            symb_select = -1;
            for (int d = 0; d < images_panel.size(); d++) {
                images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
            }
        }else {
            for (int d = 0; d < images_panel.size(); d++) {
                if (d == 7) {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.add));
                    symb_select = d;
                }else {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
                }
            }
        }
    }


    public void addTierra (View vista) {
        if (symb_select == 8) {
            symb_select = -1;
            for (int d = 0; d < images_panel.size(); d++) {
                images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
            }
        }else {
            for (int d = 0; d < images_panel.size(); d++) {
                if (d == 8) {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.add));
                    symb_select = d;
                }else {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
                }
            }
        }
    }


    public void addCable (View vista) {
        if (symb_select == 9) {
            symb_select = -1;
            for (int d = 0; d < images_panel.size(); d++) {
                images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
            }
        }else {
            for (int d = 0; d < images_panel.size(); d++) {
                if (d == 9) {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.add));
                    symb_select = d;
                }else {
                    images_panel.get(d).setBackgroundColor(ContextCompat.getColor(this, R.color.noAdd));
                }
            }
        }
    }



    public boolean dibujarCable (String orientacion, int moveimgx_ref, int moveimgy_ref, int x_cable, int y_cable) {

        if(orientacion.equals("vertical")){

            cable2.setY((moveimgy_ref)+((y_cable-(moveimgy_ref))/2.0F));
            cable2.setScaleY((y_cable-(moveimgy_ref))/grosor_cable);

            if(imgaux.getY() < moveimgy_ref){   //terminal de abajo
                termRelOrigen = 2;
                if((y_cable-(moveimgy_ref)) >= 0){      //dedo debajo del terminal de abajo
                    secuencia_cable = "yx";
                    cable2.setX(moveimgx_ref);

                    cable.setY(y_cable-(grosor_cable/2));

                    if(x_cable > moveimgx_ref) cable.setX(-2+(moveimgx_ref)+((x_cable-(moveimgx_ref))/2.0F));
                    else    cable.setX(2+(moveimgx_ref)+((x_cable-(moveimgx_ref))/2.0F));
                    cable.setScaleX((x_cable-(moveimgx_ref))/grosor_cable);

                    orientacion = "vertical";
                }
                else {      //dedo por encima del terminal de abajo
                    if(Math.abs(x_cable-(moveimgx_ref)) > 25){
                        secuencia_cable = "xy";
                        cable.setX((moveimgx_ref)+((x_cable-(moveimgx_ref))/2.0F));
                        cable.setScaleX((x_cable-(moveimgx_ref))/grosor_cable);
                        cable.setY(moveimgy_ref);

                        cable2.setX(x_cable-(grosor_cable/2));

                        if(y_cable > moveimgy_ref) cable2.setY(-2+(moveimgy_ref)+((y_cable-(moveimgy_ref))/2.0F));
                        else cable2.setY(2+(moveimgy_ref)+((y_cable-(moveimgy_ref))/2.0F));
                        cable2.setScaleY((y_cable-(moveimgy_ref))/grosor_cable);
                    }

                }
            } else {    //terminal de arriba
                termRelOrigen = 1;
                if((y_cable-(moveimgy_ref)) <= 0){      //dedo por encima del terminal de arriba
                    secuencia_cable = "yx";
                    cable2.setX(moveimgx_ref);

                    cable.setY(y_cable-(grosor_cable/2));

                    if(x_cable > moveimgx_ref) cable.setX(-2+(moveimgx_ref)+((x_cable-(moveimgx_ref))/2.0F));
                    else    cable.setX(2+(moveimgx_ref)+((x_cable-(moveimgx_ref))/2.0F));
                    cable.setScaleX((x_cable-(moveimgx_ref))/grosor_cable);

                    orientacion = "vertical";
                }
                else {      //dedo debajo del terminal de arriba
                    if(Math.abs(x_cable-(moveimgx_ref)) > 25){
                        secuencia_cable = "xy";
                        cable.setX((moveimgx_ref)+((x_cable-(moveimgx_ref))/2.0F));
                        cable.setScaleX((x_cable-(moveimgx_ref))/grosor_cable);
                        cable.setY(moveimgy_ref);

                        cable2.setX(x_cable-(grosor_cable/2));

                        if(y_cable > moveimgy_ref) cable2.setY(-2+(moveimgy_ref)+((y_cable-(moveimgy_ref))/2.0F));
                        else cable2.setY(2+(moveimgy_ref)+((y_cable-(moveimgy_ref))/2.0F));
                        cable2.setScaleY((y_cable-(moveimgy_ref))/grosor_cable);

                    }

                }
            }
            /////////////////////////////////////////////////////////////////////////////////////7
        }else{

            cable.setX((moveimgx_ref)+((x_cable-(moveimgx_ref))/2.0F));
            cable.setScaleX((x_cable-(moveimgx_ref))/grosor_cable);

            if(imgaux.getX() < moveimgx_ref){       //terminal derecho
                termRelOrigen = 2;
                if((x_cable-(moveimgx_ref)) >= 0){      //a la derecha del terminal derecho
                    secuencia_cable = "xy";
                    cable.setY(moveimgy_ref);

                    cable2.setX(x_cable-(grosor_cable/2));

                    if(y_cable > moveimgy_ref) cable2.setY(-2+(moveimgy_ref)+((y_cable-(moveimgy_ref))/2.0F));
                    else cable2.setY(2+(moveimgy_ref)+((y_cable-(moveimgy_ref))/2.0F));
                    cable2.setScaleY((y_cable-(moveimgy_ref))/grosor_cable);

                    orientacion = "horizontal";
                } else {    //a la izquierda del terminal derecho
                    if(Math.abs(y_cable-(moveimgy_ref)) > 25){
                        secuencia_cable = "yx";
                        cable2.setY((moveimgy_ref)+((y_cable-(moveimgy_ref))/2.0F));
                        cable2.setScaleY((y_cable-(moveimgy_ref))/grosor_cable);

                        cable2.setX(moveimgx_ref);

                        cable.setY(y_cable-(grosor_cable/2));

                        if(x_cable > moveimgx_ref) cable.setX(-2+(moveimgx_ref)+((x_cable-(moveimgx_ref))/2.0F));
                        else    cable.setX(2+(moveimgx_ref)+((x_cable-(moveimgx_ref))/2.0F));
                        cable.setScaleX((x_cable-(moveimgx_ref))/grosor_cable);
                    }

                }
            } else {    //terminal izquierdo
                termRelOrigen = 1;
                if((x_cable-(moveimgx_ref)) <= 0){      //a la izquierda del terminal izquierdo
                    secuencia_cable = "xy";
                    cable.setY(moveimgy_ref);

                    cable2.setX(x_cable-(grosor_cable/2));

                    if(y_cable > moveimgy_ref) cable2.setY(-2+(moveimgy_ref)+((y_cable-(moveimgy_ref))/2.0F));
                    else cable2.setY(2+(moveimgy_ref)+((y_cable-(moveimgy_ref))/2.0F));
                    cable2.setScaleY((y_cable-(moveimgy_ref))/grosor_cable);

                    orientacion = "horizontal";

                } else {    //a la derecha del terminal izquierdo
                    if(Math.abs(y_cable-(moveimgy_ref)) > 25){
                        secuencia_cable = "yx";
                        cable2.setY((moveimgy_ref)+((y_cable-(moveimgy_ref))/2.0F));
                        cable2.setScaleY((y_cable-(moveimgy_ref))/grosor_cable);

                        cable2.setX(moveimgx_ref);

                        cable.setY(y_cable-(grosor_cable/2));

                        if(x_cable > moveimgx_ref) cable.setX(-2+(moveimgx_ref)+((x_cable-(moveimgx_ref))/2.0F));
                        else    cable.setX(2+(moveimgx_ref)+((x_cable-(moveimgx_ref))/2.0F));
                        cable.setScaleX((x_cable-(moveimgx_ref))/grosor_cable);
                    }

                }
            }

        }

        return nuevoCable(x_cable,y_cable,cable,cable2,imgaux);

    }


    public boolean nuevoCable (int x_cable, int y_cable, ImageView cable, ImageView cable2, Figura imgaux) {

        boolean retorno = false;
        for(int ifig = 0; ifig < figuras.size(); ifig++){

            if(figuras.get(ifig).getAngulo() == 0 || figuras.get(ifig).getAngulo() == 180){     //vertical
                if(x_cable > figuras.get(ifig).getX() && x_cable <= (figuras.get(ifig).getX()+30) && y_cable >= figuras.get(ifig).getY() && y_cable <= (figuras.get(ifig).getY()+120)){
                    if(!(figuras.get(ifig).equals(imgaux))){
                        tView2.setText("YESSSSSSSSSSSSS");
                        if( y_cable <= (figuras.get(ifig).getY() + 25) ){
                            tView.setText("111111111111111");
                            termRelFin = 1;
                        } else if( y_cable >= (figuras.get(ifig).getY() + 90) ){
                            tView.setText("222222222222222");
                            termRelFin = 2;
                        } else {
                            return false;
                        }
                        retorno = deformarCable(imgaux,figuras.get(ifig),secuencia_cable,termRelOrigen,termRelFin,cable,cable2,cable3);
                        imgauxFin = figuras.get(ifig);
                        ifig = figuras.size();
                    }else{
                        tView2.setText("NOOOOOOOOOOOO");
                    }

                }else {
                    tView2.setText("NOOOOOOOOOOOO");
                }

            } else {    //horizontal
                if(x_cable >= (figuras.get(ifig).getX()-32) && x_cable <= (figuras.get(ifig).getX()+90) && y_cable > (figuras.get(ifig).getY()+30) && y_cable < (figuras.get(ifig).getY()+90)){
                    if(!(figuras.get(ifig).equals(imgaux))){
                        tView2.setText("YESSSSSSSSSSSSS");
                        if( x_cable <= (figuras.get(ifig).getX()) ){
                            tView.setText("111111111111111");
                            termRelFin = 1;
                        } else if( x_cable >= (figuras.get(ifig).getX() + 60) ){
                            tView.setText("222222222222222");
                            termRelFin = 2;
                        } else{
                            return false;
                        }
                        retorno = deformarCable(imgaux,figuras.get(ifig),secuencia_cable,termRelOrigen,termRelFin,cable,cable2,cable3);
                        imgauxFin = figuras.get(ifig);
                        ifig = figuras.size();
                    }else {
                        tView2.setText("NOOOOOOOOOOOO");
                    }

                }else {
                    tView2.setText("NOOOOOOOOOOOO");
                }
            }
        }

        if (!retorno) {
            cable3.setScaleX(0);
            cable3.setScaleY(0);
        }

        return retorno;

    }


    public boolean deformarCable (Figura imgOrigen, Figura imgFin, String secuencia_cable, int termRelOrigen, int termRelFin,  ImageView cable, ImageView cable2, ImageView cable3) {
        boolean retorno = true;
        float[] desp = new float[2];

        int x_termOrigen, y_termOrigen, x_termFin, y_termFin;

        if (imgOrigen.getAngulo() == 0 || imgOrigen.getAngulo() == 180) {    //Figura1 vertical
            x_termOrigen = (int) (imgOrigen.getX() + 30);
            if (termRelOrigen == 1) {      //terminal de arriba
                y_termOrigen = (int) (imgOrigen.getY());
            } else {      //terminal de abajo
                y_termOrigen = (int) (imgOrigen.getY() + 120);
            }

            if (imgFin.getAngulo() == 90 || imgFin.getAngulo() == 270) {        //Figura2 horizontal
                y_termFin = (int) (imgFin.getY() + 60);
                if (termRelFin == 1) {      //terminal izquierdo
                    x_termFin = (int) (imgFin.getX() - 30);
                } else {      //terminal derecho
                    x_termFin = (int) (imgFin.getX() + 90);
                }


                if (x_termOrigen == x_termFin){
                    if(termRelOrigen == 1 && (y_termOrigen < y_termFin) ) {
                        tView2.setText(".............FALSE");
                        return false;
                    } else if(termRelOrigen == 2 && (y_termOrigen > y_termFin) ) {
                        tView2.setText(".............FALSE");
                        return false;
                    }
                }


                if (x_termOrigen < x_termFin) {     //terminal Figura1 <-  -> terminal Figura2
                    if (termRelFin == 2) {      //terminal derecho Figura2  (b2)
                        if (termRelOrigen == 1 && y_termOrigen >= y_termFin){      //a1 true && y_a1 >= y_b2
                            //cable3
                            if(y_termOrigen == y_termFin)   desp = cableAdicional("vertical",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("vertical",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            tView2.setText("CABLE3......");
                        } else if (termRelOrigen == 2 && y_termOrigen <= y_termFin){     //b1 true && y_b1 <= y_b2
                            //cable3
                            if(y_termOrigen == y_termFin)   desp = cableAdicional("vertical",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("vertical",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            tView2.setText("CABLE3......");
                        }
                    }

                }else if (x_termOrigen > x_termFin) {     //terminal Figura2 <-  -> terminal Figura1
                    if (termRelFin == 1) {      //terminal izquierdo Figura2  (a2)
                        if (termRelOrigen == 1 && y_termOrigen >= y_termFin){      //a1 true && y_a1 >= y_a2
                            //cable3
                            if(y_termOrigen == y_termFin)   desp = cableAdicional("vertical",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("vertical",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            tView2.setText("CABLE3......");
                        } else if (termRelOrigen == 2 && y_termOrigen <= y_termFin){     //b1 true && y_b1 <= y_a2
                            //cable3
                            if(y_termOrigen == y_termFin)   desp = cableAdicional("vertical",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("vertical",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            tView2.setText("CABLE3......");
                        }
                    }
                }


            } else {    //Figura2 vertical
                x_termFin = (int) (imgFin.getX() + 30);
                if (termRelFin == 1) {      //terminal de arriba
                    y_termFin = (int) (imgFin.getY());
                } else {      //terminal de abajo
                    y_termFin = (int) (imgFin.getY() + 120);
                }

                if (x_termOrigen != x_termFin) {
                    if(termRelOrigen == 1 && termRelFin == 2 && (y_termOrigen < y_termFin)) {    //a1 true && b2 true && (y_a1 < y_b2)
                        //cable3
                        tView2.setText("CABLE3......");
                        if (x_termOrigen < x_termFin) {
                            if (x_termOrigen == (x_termFin-30))     desp = cableAdicional("horizontal",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("horizontal",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                        } else {
                            if (x_termOrigen == (x_termFin+30))     desp = cableAdicional("horizontal",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("horizontal",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                        }
                    }else if(termRelOrigen == 2 && termRelFin == 1 && (y_termOrigen > y_termFin)) {    //b1 true && a2 true && (y_b1 > y_a2)
                        //cable3
                        tView2.setText("CABLE3......");
                        if (x_termOrigen < x_termFin) {
                            if (x_termOrigen == (x_termFin-30))     desp = cableAdicional("horizontal",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("horizontal",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                        } else {
                            if (x_termOrigen == (x_termFin+30))     desp = cableAdicional("horizontal",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("horizontal",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                        }
                    }
                }else {
                    if (imgOrigen.getY() < imgFin.getY()) {     //Fig1 ^  v Fig2
                        if (termRelOrigen == 1){    //a1 true
                            retorno = false;
                            tView2.setText(".............FALSE");
                        } else {    //b1 true
                            if (termRelFin == 2) {  //b2 true
                                retorno = false;
                                tView2.setText(".............FALSE");
                            }
                        }

                    } else {     //Fig2 ^  v Fig1
                        if (termRelOrigen == 2){    //b1 true
                            retorno = false;
                            tView2.setText(".............FALSE");
                        } else {    //a1 true
                            if (termRelFin == 1) {  //a2 true
                                retorno = false;
                                tView2.setText(".............FALSE");
                            }
                        }
                    }
                }
            }


        } else { //-----------------------Figura 1 horizontal---------------------------------------

            y_termOrigen = (int) (imgOrigen.getY() + 60);
            if (termRelOrigen == 1) {      //terminal izquierdo
                x_termOrigen = (int) (imgOrigen.getX() - 30);
            } else {      //terminal derecho
                x_termOrigen = (int) (imgOrigen.getX() + 90);
            }

            if (imgFin.getAngulo() == 0 || imgFin.getAngulo() == 180) {        //Figura2 vertical
                x_termFin = (int) (imgFin.getX() + 30);
                if (termRelFin == 1) {      //terminal de arriba
                    y_termFin = (int) (imgFin.getY() );
                } else {      //terminal de abajo
                    y_termFin = (int) (imgFin.getY() + 120);
                }


                if (y_termOrigen == y_termFin){
                    if(termRelOrigen == 1 && (x_termOrigen < x_termFin) ) {
                        tView2.setText(".............FALSE");
                        return false;
                    } else if(termRelOrigen == 2 && (x_termOrigen > x_termFin) ) {
                        tView2.setText(".............FALSE");
                        return false;
                    }
                }


                if (y_termOrigen < y_termFin) {     //terminal Figura1 ^  v terminal Figura2
                    if (termRelFin == 2) {      //terminal abajo Figura2  (2b)
                        if (termRelOrigen == 1 && x_termOrigen >= x_termFin){      //1a true && x_1a >= x_2b
                            //cable3
                            if(x_termOrigen == x_termFin)   desp = cableAdicional("horizontal",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("horizontal",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            tView2.setText("CABLE3......Fig2vert");
                        } else if (termRelOrigen == 2 && x_termOrigen <= x_termFin){     //1b true && x_b1 <= x_2b
                            //cable3
                            if(x_termOrigen == x_termFin)   desp = cableAdicional("horizontal",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("horizontal",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            tView2.setText("CABLE3......Fig2vert");
                        }
                    }

                }else if (y_termOrigen > y_termFin) {     //terminal Figura2 ^  v terminal Figura1
                    if (termRelFin == 1) {      //terminal izquierdo Figura2  (2a)
                        if (termRelOrigen == 1 && x_termOrigen >= x_termFin){      //1a true && x_1a >= x_2a
                            //cable3
                            if(x_termOrigen == x_termFin)   desp = cableAdicional("horizontal",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("horizontal",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            tView2.setText("CABLE3......Fig2vert");
                        } else if (termRelOrigen == 2 && x_termOrigen <= x_termFin){     //1b true && x_1b <= x_2a
                            //cable3
                            if(x_termOrigen == x_termFin)   desp = cableAdicional("horizontal",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("horizontal",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            tView2.setText("CABLE3......Fig2vert");
                        }
                    }
                }


            } else {    //Figura2 horizontal
                y_termFin = (int) (imgFin.getY() + 60);
                if (termRelFin == 1) {      //terminal izquiero
                    x_termFin = (int) (imgFin.getX() - 30);
                } else {      //terminal derecho
                    x_termFin = (int) (imgFin.getX() + 90);
                }

                if (y_termOrigen != y_termFin) {
                    if(termRelOrigen == 1 && termRelFin == 2 && (x_termOrigen < x_termFin)) {    //1a true && 2b true && (x_1a < x_2b)
                        //cable3
                        if (y_termOrigen < y_termFin) {
                            if (y_termOrigen == (y_termFin-30))     desp = cableAdicional("vertical",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("vertical",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                        } else {
                            if (y_termOrigen == (y_termFin+30))     desp = cableAdicional("vertical",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("vertical",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                        }
                        tView2.setText("CABLE3......Fig2hor");
                    }else if(termRelOrigen == 2 && termRelFin == 1 && (x_termOrigen > x_termFin)) {    //1b true && 2a true && (x_1b > x_2a)
                        //cable3
                        tView2.setText("CABLE3......Fig2hor");
                        if (y_termOrigen < y_termFin) {
                            if (y_termOrigen == (y_termFin-30))     desp = cableAdicional("vertical",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("vertical",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                        } else {
                            if (y_termOrigen == (y_termFin+30))     desp = cableAdicional("vertical",1,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                            else    desp = cableAdicional("vertical",2,imgOrigen,imgFin,secuencia_cable,termRelOrigen,termRelFin,cable3);
                        }
                    }
                }else {
                    if (imgOrigen.getX() < imgFin.getX()) {     //Fig1 <  > Fig2
                        if (termRelOrigen == 1){    //1a true
                            retorno = false;
                            tView2.setText(".............FALSE");
                        } else {    //1b true
                            if (termRelFin == 2) {  //2b true
                                retorno = false;
                                tView2.setText(".............FALSE");
                            }
                        }

                    } else {     //Fig2 <  > Fig1
                        if (termRelOrigen == 2){    //1b true
                            retorno = false;
                            tView2.setText(".............FALSE");
                        } else {    //1a true
                            if (termRelFin == 1) {  //2a true
                                retorno = false;
                                tView2.setText(".............FALSE");
                            }
                        }
                    }
                }
            }
        }



        //////////////////////////////////////////////////////////////////////////////////////////////////////////////


        if(secuencia_cable.equals("xy")){
            if (imgFin.getAngulo() == 0 || imgFin.getAngulo() == 180) {     //Fig2 vertical
                if(termRelFin == 1){
                    cable.setX((moveimgx_ref)+(( (imgFin.getX()+30 + desp[0]) -(moveimgx_ref))/2.0F));
                    cable.setScaleX(( (imgFin.getX()+30 + desp[0]) -(moveimgx_ref))/grosor_cable);
                    cable.setY(moveimgy_ref);

                    cable2.setX( (imgFin.getX()+30 + desp[0]) -(grosor_cable/2));

                    if(imgFin.getY() > moveimgy_ref) cable2.setY(-2+(moveimgy_ref)+(( imgFin.getY() -(moveimgy_ref))/2.0F));
                    else cable2.setY(2+(moveimgy_ref)+(( imgFin.getY() -(moveimgy_ref))/2.0F));
                    cable2.setScaleY(( imgFin.getY() -(moveimgy_ref))/grosor_cable);

                } else if(termRelFin == 2){
                    cable.setX((moveimgx_ref)+(( (imgFin.getX()+30 + desp[0]) -(moveimgx_ref))/2.0F));
                    cable.setScaleX(( (imgFin.getX()+30 + desp[0]) -(moveimgx_ref))/grosor_cable);
                    cable.setY(moveimgy_ref);

                    cable2.setX( (imgFin.getX()+30 + desp[0]) -(grosor_cable/2));

                    if((imgFin.getY()+120) > moveimgy_ref) cable2.setY(-2+(moveimgy_ref)+(( (imgFin.getY()+120) -(moveimgy_ref))/2.0F));
                    else cable2.setY(2+(moveimgy_ref)+(( (imgFin.getY()+120) -(moveimgy_ref))/2.0F));
                    cable2.setScaleY(( (imgFin.getY()+120) -(moveimgy_ref))/grosor_cable);
                }

            } else {        //Fig2 horizontal

                if(termRelFin == 1){
                    cable.setX((moveimgx_ref)+(( (imgFin.getX()-30) -(moveimgx_ref))/2.0F));
                    cable.setScaleX(( (imgFin.getX()-30) -(moveimgx_ref))/grosor_cable);
                    cable.setY(moveimgy_ref + desp[1]);

                    if (y_termOrigen == y_termFin && desp[1] != 0)  cable2.setX( moveimgx_ref );
                    else    cable2.setX( (imgFin.getX()-30) -(grosor_cable/2));

                    if((imgFin.getY()+60) > moveimgy_ref) cable2.setY(-2+(moveimgy_ref)+(( (imgFin.getY()+60 + desp[1]) -(moveimgy_ref))/2.0F));
                    else cable2.setY(2+(moveimgy_ref)+(( (imgFin.getY()+60 + desp[1]) -(moveimgy_ref))/2.0F));
                    cable2.setScaleY(( (imgFin.getY()+60 + desp[1]) -(moveimgy_ref))/grosor_cable);

                } else if(termRelFin == 2){
                    cable.setX((moveimgx_ref)+(( (imgFin.getX()+90) -(moveimgx_ref))/2.0F));
                    cable.setScaleX(( (imgFin.getX()+90) -(moveimgx_ref))/grosor_cable);
                    cable.setY(moveimgy_ref + desp[1]);

                    if (y_termOrigen == y_termFin && desp[1] != 0)  cable2.setX( moveimgx_ref );
                    else    cable2.setX( (imgFin.getX()+90) -(grosor_cable/2));

                    if((imgFin.getY()+60) > moveimgy_ref) cable2.setY(-2+(moveimgy_ref)+(( (imgFin.getY()+60 + desp[1]) -(moveimgy_ref))/2.0F));
                    else cable2.setY(2+(moveimgy_ref)+(( (imgFin.getY()+60 + desp[1]) -(moveimgy_ref))/2.0F));
                    cable2.setScaleY(( (imgFin.getY()+60 + desp[1]) -(moveimgy_ref))/grosor_cable);
                }
            }

            //-----------------------------------------------------------------------------------

        } else if (secuencia_cable.equals("yx")) {
            if (imgFin.getAngulo() == 0 || imgFin.getAngulo() == 180) {     //Fig2 vertical
                if(termRelFin == 1){
                    cable2.setY((moveimgy_ref)+(( imgFin.getY() -(moveimgy_ref))/2.0F));
                    cable2.setScaleY(( imgFin.getY() -(moveimgy_ref))/grosor_cable);
                    cable2.setX(moveimgx_ref + desp[0]);

                    if (x_termOrigen == x_termFin && desp[0] != 0)  cable.setY( moveimgy_ref );
                    else    cable.setY( imgFin.getY() -(grosor_cable/2));

                    if( (imgFin.getX()+30) > moveimgx_ref) cable.setX(-2+(moveimgx_ref)+(( (imgFin.getX()+30 + desp[0]) -(moveimgx_ref))/2.0F));
                    else    cable.setX(2+(moveimgx_ref)+(( (imgFin.getX()+30 + desp[0]) -(moveimgx_ref))/2.0F));
                    cable.setScaleX(( (imgFin.getX()+30 + desp[0]) -(moveimgx_ref))/grosor_cable);

                } else if(termRelFin == 2){
                    cable2.setY((moveimgy_ref)+(( (imgFin.getY()+120) -(moveimgy_ref))/2.0F));
                    cable2.setScaleY(( (imgFin.getY()+120) -(moveimgy_ref))/grosor_cable);
                    cable2.setX(moveimgx_ref + desp[0]);

                    if (x_termOrigen == x_termFin && desp[0] != 0)  cable.setY( moveimgy_ref );
                    else    cable.setY( (imgFin.getY()+120) -(grosor_cable/2));

                    if( (imgFin.getX()+30)  > moveimgx_ref) cable.setX(-2+(moveimgx_ref)+(( (imgFin.getX()+30 + desp[0]) -(moveimgx_ref))/2.0F));
                    else    cable.setX(2+(moveimgx_ref)+(( (imgFin.getX()+30 + desp[0]) -(moveimgx_ref))/2.0F));
                    cable.setScaleX(( (imgFin.getX()+30 + desp[0]) -(moveimgx_ref))/grosor_cable);
                }

            } else {        //Fig2 horizontal

                if(termRelFin == 1){
                    cable2.setY((moveimgy_ref)+(( (imgFin.getY()+60 + desp[1]) -(moveimgy_ref))/2.0F));
                    cable2.setScaleY(( (imgFin.getY()+60 + desp[1]) -(moveimgy_ref))/grosor_cable);
                    cable2.setX(moveimgx_ref);

                    cable.setY( (imgFin.getY()+60 + desp[1]) -(grosor_cable/2));

                    if( (imgFin.getX()-30) > moveimgx_ref) cable.setX(-2+(moveimgx_ref)+(( (imgFin.getX()-30) -(moveimgx_ref))/2.0F));
                    else    cable.setX(2+(moveimgx_ref)+(( (imgFin.getX()-30) -(moveimgx_ref))/2.0F));
                    cable.setScaleX(( (imgFin.getX()-30) -(moveimgx_ref))/grosor_cable);

                } else if(termRelFin == 2){
                    cable2.setY((moveimgy_ref)+(( (imgFin.getY()+60 + desp[1]) -(moveimgy_ref))/2.0F));
                    cable2.setScaleY(( (imgFin.getY()+60 + desp[1]) -(moveimgy_ref))/grosor_cable);
                    cable2.setX(moveimgx_ref);

                    cable.setY( (imgFin.getY()+60 + desp[1]) -(grosor_cable/2));

                    if( (imgFin.getX()+90) > moveimgx_ref) cable.setX(-2+(moveimgx_ref)+(( (imgFin.getX()+90) -(moveimgx_ref))/2.0F));
                    else    cable.setX(2+(moveimgx_ref)+(( (imgFin.getX()+90) -(moveimgx_ref))/2.0F));
                    cable.setScaleX(( (imgFin.getX()+90) -(moveimgx_ref))/grosor_cable);
                }
            }
        }

        // RETOQUES------------------------------------------------

        if (x_termFin < moveimgx_ref) {
            cable.setX(cable.getX()-(1+grosor_cable/2.0F));
            cable.setScaleX(cable.getScaleX() - 0.7F);
        }
        if (y_termFin < moveimgy_ref) {
            cable2.setY(cable2.getY()-(1+grosor_cable/2.0F));
            cable2.setScaleY(cable2.getScaleY() - 0.7F);
        }

        /*
        if(cable3.getScaleX() == 1.0F) {    //cable3 vertical
            cable3.setScaleY(cable3.getScaleY() + (0.3F*Math.abs(cable3.getScaleY())/cable3.getScaleY()));
        } else {        //cable3 horizontal
            cable3.setScaleX(cable3.getScaleX() + (0.3F*Math.abs(cable3.getScaleX())/cable3.getScaleX()));
        }
        */


        tView2.setText("desp0: " + desp[0] + "   desp1: " + desp[1]);
        //tView2.setText("..........." + secuencia_cable);
        return retorno;

    }


    public float[] cableAdicional (String orientC3, int ladoC3, Figura imgOrigen, Figura imgFin, String secuencia_cable, int termRelOrigen, int termRelFin, ImageView cable3) {
        float[] desp = new float[2];
        desp[0] = 0;
        desp[1] = 0;

        cable3.setScaleX(0);
        cable3.setScaleY(0);

        int desp_c3 = 2;

        if(orientC3.equals("vertical")) {   //cable3 vertical   (Fig2 horizontal)
            cable3.setScaleX(1.0F);

            if(termRelFin == 1) {   //terminal izquierdo
                cable3.setX( (imgFin.getX() - 30) - (grosor_cable/2));     //setX(terminal izquierdo - mitad del cable)
            } else {    //terminal derecho
                cable3.setX( (imgFin.getX() + 90) - (grosor_cable/2));     //setX(terminal derecho - mitad del cable)
            }

            if(ladoC3 == 1) {   //lado superior de Fig2
                cable3.setY(-desp_c3+(imgFin.getY()+60/*origen*/) + (( (imgFin.getY()+30/*fin*/)-(imgFin.getY()+60/*origen*/))/2.0F));
                cable3.setScaleY(( (-desp_c3+imgFin.getY()+30/*fin*/)-(desp_c3+imgFin.getY()+60/*origen*/))/grosor_cable);
                desp[1] = -30;     //desplazamiento hacia arriba en Y

            } else if(ladoC3 == 2) {   //lado inferior de Fig2
                cable3.setY(-desp_c3+(imgFin.getY()+60/*origen*/) + (( (imgFin.getY()+90/*fin*/)-(imgFin.getY()+60/*origen*/))/2.0F));
                cable3.setScaleY(( (desp_c3+imgFin.getY()+90/*fin*/)-(-desp_c3+imgFin.getY()+60/*origen*/))/grosor_cable);
                desp[1] = 30;      //desplazamiento hacia abajo en Y
            }


        }else if(orientC3.equals("horizontal")) {   //cable3 horizontal   (Fig2 vertical)
            cable3.setScaleY(1.0F);

            if(termRelFin == 1) {   //terminal de arriba
                cable3.setY( (imgFin.getY() ) - (grosor_cable/2));     //setX(terminal de arriba - mitad del cable)
            } else {    //terminal de abajo
                cable3.setY( (imgFin.getY() + 120) - (grosor_cable/2));     //setX(terminal de abajo - mitad del cable)
            }

            if(ladoC3 == 1) {   //lado izquierdo de Fig2
                cable3.setX(-desp_c3+(imgFin.getX()+30/*origen*/) + (( (imgFin.getX()/*fin*/)-(imgFin.getX()+30/*origen*/))/2.0F));
                cable3.setScaleX(( (-desp_c3+imgFin.getX()/*fin*/)-(desp_c3+imgFin.getX()+30/*origen*/))/grosor_cable);
                desp[0] = -30;     //desplazamiento hacia la izquierda en X

            } else if(ladoC3 == 2) {   //lado derecho de Fig2
                cable3.setX(-desp_c3+(imgFin.getX()+30/*origen*/) + (( (imgFin.getX()+60/*fin*/)-(imgFin.getX()+30/*origen*/))/2.0F));
                cable3.setScaleX(( (desp_c3+imgFin.getX()+60/*fin*/)-(-desp_c3+imgFin.getX()+30/*origen*/))/grosor_cable);
                desp[0] = 30;  //desplazamiento hacia la derecha en X
            }

        }

        return desp;

    }


    public void construirCable (int idCable) {

        for (int iCab = 0; iCab < cables.size(); iCab++) {
            if(idCable == cables.get(iCab).getIdCable()) {
                ImageView CA = new ImageView(this);
                CA.setImageResource(R.drawable.cable);
                constraint_canvas.addView(CA);
                CA.setLayoutParams(new ConstraintLayout.LayoutParams((int)grosor_cable,(int)grosor_cable));

                CA.setX(cables.get(iCab).getA_cable());
                CA.setY(cables.get(iCab).getB_cable());
                CA.setScaleX(cables.get(iCab).getA2_cable());

                ImageView CB = new ImageView(this);
                CB.setImageResource(R.drawable.cable);
                constraint_canvas.addView(CB);
                CB.setLayoutParams(new ConstraintLayout.LayoutParams((int)grosor_cable,(int)grosor_cable));

                CB.setX(cables.get(iCab).getA_cable2());
                CB.setY(cables.get(iCab).getB_cable2());
                CB.setScaleY(cables.get(iCab).getB2_cable2());

                ImageView CC = new ImageView(this);
                CC.setImageResource(R.drawable.cable);
                constraint_canvas.addView(CC);
                CC.setLayoutParams(new ConstraintLayout.LayoutParams((int)grosor_cable,(int)grosor_cable));

                CC.setX(cables.get(iCab).getA_cable3());
                CC.setY(cables.get(iCab).getB_cable3());
                CC.setScaleX(cables.get(iCab).getA2_cable3());
                CC.setScaleY(cables.get(iCab).getB2_cable3());


                CA.setId(idCX++);
                CB.setId(idCX++);
                CC.setId(idCX++);


                cables.get(iCab).setIdC1(CA.getId());
                cables.get(iCab).setIdC2(CB.getId());
                cables.get(iCab).setIdC3(CC.getId());

                cablesSecundarios.add(CA);
                cablesSecundarios.add(CB);
                cablesSecundarios.add(CC);

                //tView.setVisibility(View.VISIBLE);
                //tView.setText("////// " + cablesSecundarios.size() + "////// " + cables.size());
                //tView.setText("//" + CA.getId() + "//" + CB.getId() + "//" + CC.getId());
                tView4.setText("Cables: " + cables.size());

            }
        }

    }


    public void eliminarCable (Figura figura) {

        for (int intEC = 0; intEC < (figuras.size())*2; intEC++){     //trampa (para mayor eficiencia, buscar como solucionar el problema: "no se eliminan
            //todos los cables de los dos terminales de una figura ELIMINADA/ROTADA")

            for (int iCab = 0; iCab < cables.size(); iCab++) {

                if (cables.get(iCab).getIdOrigen() == figura.getID() || cables.get(iCab).getIdFin() == figura.getID()) {

                    for (int iCabSec = 0; iCabSec < cablesSecundarios.size(); iCabSec++) {

                        if (cablesSecundarios.get(iCabSec).getId() == cables.get(iCab).getIdC1()) {
                            cablesSecundarios.get(iCabSec).setScaleX(0);
                            cablesSecundarios.get(iCabSec).setScaleY(0);
                            cablesSecundarios.remove(iCabSec);
                            //cables.remove(iCab);

                        }
                        if (cablesSecundarios.get(iCabSec).getId() == cables.get(iCab).getIdC2()) {
                            cablesSecundarios.get(iCabSec).setScaleX(0);
                            cablesSecundarios.get(iCabSec).setScaleY(0);
                            cablesSecundarios.remove(iCabSec);

                        }
                        if (cablesSecundarios.get(iCabSec).getId() == cables.get(iCab).getIdC3()) {
                            cablesSecundarios.get(iCabSec).setScaleX(0);
                            cablesSecundarios.get(iCabSec).setScaleY(0);
                            cablesSecundarios.remove(iCabSec);
                            cables.remove(iCab);
                            iCabSec = cablesSecundarios.size();
                        }

                    }

                    //iCab = cables.size();
                }

            }

        }


        tView4.setText("Cables: " + cables.size());

    }


    public void eliminarFigura (View vista) {

        if (imgaux != null && figuras.size() > 0) {

            for (int iFig = 0; iFig < figuras.size(); iFig++) {

                if (figuras.get(iFig).getID() == imgaux.getID()) {
                    eliminarCable(imgaux);
                    figuras.get(iFig).setVisibility(View.GONE);
                    //tView.setVisibility(View.VISIBLE);
                    //tView.setText("" + figuras.get(iFig).getID());
                    figuras.remove(iFig);
                    imgaux = null;
                    iFig = figuras.size();
                }

            }


            //imgaux.setVisibility(View.GONE);

            tView3.setText("Figuras: " + figuras.size());
        }

    }



    //Mi ObjectAnimator (MOB)   (el ObjectAnimator ORIGINAL tiene problemas cuando se activa el modo de ahorro de bater�a)
    public void animMOB (final View vistaMOB, final String eje, float inicio, float fin, int duracion /*milisegundos*/) {
        v_animMOB = ((fin - inicio)/duracion);      //velocidad de incremento
        incremento_animMOB = inicio;
        TanimMOB = new Timer();
        TanimMOB.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(eje == "TranslationX"){
                            vistaMOB.setX(incremento_animMOB);
                            incremento_animMOB = incremento_animMOB + v_animMOB;
                        }else if(eje == "TranslationY"){
                            vistaMOB.setY(incremento_animMOB);
                            incremento_animMOB = incremento_animMOB + v_animMOB;
                        }
                    }
                });
            }
        }, 1, 1);
    }


    public void recenter (View vista) {
        constraint_canvas.setX((width_disp-1200)/2);
        constraint_canvas.setY((height_disp-780)/2);
        mScaleFactor = 0.5F;
        a_canvas2 = mScaleFactor;
        b_canvas2 = mScaleFactor;
        constraint_canvas.setScaleX(a_canvas2);
        constraint_canvas.setScaleY(b_canvas2);
        /*
        ViewGroup.LayoutParams params = constraint_canvas.getLayoutParams();
        params.width = 600;
        constraint_canvas.requestLayout();
        */
    }

    public void rotarAux (View vista) {

        if (imgaux != null) {
            imgaux.rotar();
            eliminarCable(imgaux);
        }

    }


    public class Cuadricula extends View {

        public Cuadricula(Context context) {

            super(context);
        }

        protected void onDraw(Canvas canvas) {


            //cuadriculas
            Paint miPincelcuad = new Paint();
            //miPincelcuad.setColor(Color.GRAY);
            miPincelcuad.setColor(new Color().argb(20,0,0,0));      //argb(int alpha, int red, int green, int blue)
            miPincelcuad.setStrokeWidth(3);     //establece grosor del trazo
            miPincelcuad.setStyle(Paint.Style.STROKE);      //Paint.Style.STROKE sirve
            //para dibujar contorno, si se quisiera rellenar,
            //se usaria Paint.Style.FILL; y en el caso
            //de querer dibujar ambas, se usaria Paint.Style.FILL_AND_STROKE

            //lineas verticales y horizontales
            for (int i = 0; i < 2000; i += 30) {
                miPincelcuad.setStrokeWidth(2);
                canvas.drawLine(i, 0, i, 2000, miPincelcuad);   //drawLine(float startX, float startY, float stopX, float stopY, Paint paint)
                canvas.drawLine(0, i, 2000, i, miPincelcuad);

                miPincelcuad.setStrokeWidth(2);
                canvas.drawLine(i+15, 0, i+15, 2000, miPincelcuad);   //drawLine(float startX, float startY, float stopX, float stopY, Paint paint)
                canvas.drawLine(0, i+15, 2000, i+15, miPincelcuad);

            }

        }
    }

    public class Figura extends android.support.v7.widget.AppCompatImageView {

        private int ID, idRecurso;
        private float X, Y;
        int angulo;

        public Figura(Context context, int idRecurso, int ID) {
            super(context);
            this.ID = ID;
            this.idRecurso = idRecurso;
            super.setImageResource(idRecurso);
            //super.setLayoutParams(new ConstraintLayout.LayoutParams(60,120));
            //figuras.add(this);
            angulo = 0;
        }

        private int getID () {
            return ID;
        }

        public int getIdRecurso() {
            return idRecurso;
        }

        @Override
        public float getX() {
            return X;
        }

        @Override
        public float getY() {
            return Y;
        }

        @Override
        public void setX(float X){
            super.setX(X);
            this.X = X;
        }

        @Override
        public void setY(float Y){
            super.setY(Y);
            this.Y = Y;
        }

        public void rotar (){
            angulo = (angulo + 90) % 360;
            super.setRotation(angulo);
        }

        public int getAngulo() {
            return angulo;
        }
    }

    public class Cable {

        private int idOrigen, idFin, terminalOrigen, terminalFin;
        private int idCable;
        private int a_cable, b_cable, a_cable2, b_cable2, a2_cable, b2_cable2, a_cable3, b_cable3, a2_cable3, b2_cable3;
        private int idC1, idC2, idC3;

        public Cable (Figura imgaux, Figura imgauxFin, int idCable, ImageView cable, ImageView cable2,ImageView cable3) {
            idOrigen = imgaux.getID();
            idFin = imgauxFin.getID();
            this.idCable = idCable;

            this.a_cable = (int) cable.getX();
            this.b_cable = (int) cable.getY();

            this.a_cable2 = (int) cable2.getX();
            this.b_cable2 = (int) cable2.getY();

            this.a2_cable = (int) cable.getScaleX();
            this.b2_cable2 = (int) cable2.getScaleY();

            this.a_cable3 = (int) cable3.getX();
            this.b_cable3 = (int) cable3.getY();
            this.a2_cable3 = (int) cable3.getScaleX();
            this.b2_cable3 = (int) cable3.getScaleY();

        }

        public int getIdCable () {
            return idCable;
        }

        public int getA_cable () {
            return a_cable;
        }
        public int getB_cable () {
            return b_cable;
        }
        public int getA_cable2 () {
            return a_cable2;
        }
        public int getB_cable2 () {
            return b_cable2;
        }
        public int getA2_cable () {
            return a2_cable;
        }
        public int getB2_cable2 () {
            return b2_cable2;
        }
        public int getA_cable3 () {
            return a_cable3;
        }
        public int getB_cable3() {
            return b_cable3;
        }
        public int getA2_cable3() {
            return a2_cable3;
        }
        public int getB2_cable3() {
            return b2_cable3;
        }

        public int getIdC1() {
            return idC1;
        }
        public int getIdC2() {
            return idC2;
        }
        public int getIdC3() {
            return idC3;
        }

        public int getIdOrigen () {
            return idOrigen;
        }

        public int getIdFin () {
            return idFin;
        }

        public int getTerminalOrigen () {
            return terminalOrigen;
        }

        public int getTerminalFin () {
            return terminalFin;
        }

        //SETTERS----------------------------------------------
        public void setIdC1(int idC1) {
            this.idC1 = idC1;
        }
        public void setIdC2(int idC2) {
            this.idC2 = idC2;
        }
        public void setIdC3(int idC3) {
            this.idC3 = idC3;
        }
    }

    public class LineaVertical extends View {

        public LineaVertical(Context context) {

            super(context);
        }

        protected void onDraw(Canvas canvas) {


            //cuadriculas
            Paint miPincelcuad = new Paint();
            //miPincelcuad.setColor(Color.GRAY);
            //miPincelcuad.setColor(new Color().argb(100,192,189,189));      //argb(int alpha, int red, int green, int blue)
            miPincelcuad.setColor(ContextCompat.getColor(contexto, R.color.noAdd));
            miPincelcuad.setStrokeWidth(180);     //establece grosor del trazo
            miPincelcuad.setStyle(Paint.Style.STROKE);      //Paint.Style.STROKE sirve
            //para dibujar contorno, si se quisiera rellenar,
            //se usaria Paint.Style.FILL; y en el caso
            //de querer dibujar ambas, se usaria Paint.Style.FILL_AND_STROKE

            canvas.drawLine(33, 0, 33, 2000, miPincelcuad);   //drawLine(float startX, float startY, float stopX, float stopY, Paint paint)

        }
    }

    public class LineaHorizontal extends View {

        public LineaHorizontal(Context context) {

            super(context);
        }

        protected void onDraw(Canvas canvas) {


            //cuadriculas
            Paint miPincelcuad = new Paint();
            //miPincelcuad.setColor(Color.GRAY);
            //miPincelcuad.setColor(new Color().argb(100,192,189,189));      //argb(int alpha, int red, int green, int blue)
            miPincelcuad.setColor(ContextCompat.getColor(contexto, R.color.noAdd));
            miPincelcuad.setStrokeWidth(180);     //establece grosor del trazo
            miPincelcuad.setStyle(Paint.Style.STROKE);      //Paint.Style.STROKE sirve
            //para dibujar contorno, si se quisiera rellenar,
            //se usaria Paint.Style.FILL; y en el caso
            //de querer dibujar ambas, se usaria Paint.Style.FILL_AND_STROKE

            canvas.drawLine(0, 33, 2000, 33, miPincelcuad);   //drawLine(float startX, float startY, float stopX, float stopY, Paint paint)

        }
    }
}