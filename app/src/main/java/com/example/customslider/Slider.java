package com.example.customslider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class Slider extends View {



    // Dimensions minimales du slider (en dp).
    // Uniquement les éléments critiques
    final static float MIN_BAR_LENGTH = 160;
    final static float MIN_CURSOR_DIAMETER = 30;

    // Dimensions par défaut du slider (en dp)
    final static float DEFAULT_BAR_WIDTH = 20;
    final static float DEFAULT_BAR_LENGTH = 160;
    final static float DEFAULT_CURSOR_DIAMETER = 40;

    // Longueur de la glissière du slider
    private float mBarLength;

    // Largeur de la glissière du slider
    private float mBarWidth;

    // Diamètre de la poignée du slider
    private float mCursorDiameter;

    // Pinceaux
    private Paint mCursorPaint = null;
    private Paint mValueBarPaint = null;
    private Paint mBarPaint = null;

    // Couleurs
    private int mDisabledColor;
    private int mCursorColor;
    private int mBarColor;
    private int mValueBarColor;


    // Etat du Slider
    private boolean mEnabled = true;


    // Valeur du Slider
    private float mValue = 50;

    // Valeur minimale du Slider
    private float mMin = 0;

    // Valeur maximale du Slider
    private float mMax = 100;

    // Etats spécifiques aux actions
    private boolean isDoubleClick = false;
    private boolean moveActionDisabled = false;


    /**
     * Constructeur dynamique
     * @param context
     */
    public Slider(Context context) {
        super(context);
        init(context,null);
    }

    /**
     * Constructeur statique (via XML)
     * @param context : contexte du slider
     * @param attrs : paramètres de personnalisation issus du XML
     */
    public Slider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }


    /**
     * Initialisation du Slider : code mutualisé entre constructeurs
     * @param context
     * @param attributeSet
     */
    private void init(Context context, AttributeSet attributeSet){

        // Initialisation des dimensions par défaut en pixel
        mBarLength = dpToPixel(DEFAULT_BAR_LENGTH);
        mCursorDiameter = dpToPixel(DEFAULT_CURSOR_DIAMETER);
        mBarWidth = dpToPixel(DEFAULT_BAR_WIDTH);

        mCursorPaint = new Paint();
        mBarPaint = new Paint();
        mValueBarPaint = new Paint();

        // Suppression du repliement
        mCursorPaint.setAntiAlias(true);
        mBarPaint.setAntiAlias(true);
        mValueBarPaint.setAntiAlias(true);

        // Application du style (plein)
        mValueBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setStyle(Paint.Style.STROKE);
        mCursorPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // Couleurs par défaut
        mDisabledColor = ContextCompat.getColor(context, R.color.colorDisabled);
        mCursorColor = ContextCompat.getColor(context, R.color.colorAccent);
        mBarColor = ContextCompat.getColor(context, R.color.colorPrimary);
        mValueBarColor = ContextCompat.getColor(context, R.color.colorSecondary);

        mBarPaint.setColor(mBarColor);
        mValueBarPaint.setColor(mValueBarColor);
        mCursorPaint.setColor(mCursorColor);


        // Spécification des terminaisons
        mBarPaint.setStrokeCap(Paint.Cap.ROUND);
        mValueBarPaint.setStrokeCap(Paint.Cap.ROUND);



        // Fixe les largeurs
        mBarPaint.setStrokeWidth(mBarWidth);
        mValueBarPaint.setStrokeWidth(mBarWidth);

        // Initialisation des dimensions minimales
        int minWidth = (int) dpToPixel(MIN_CURSOR_DIAMETER)+ getPaddingLeft() + getPaddingRight();
        int minHeight = (int) dpToPixel(MIN_BAR_LENGTH + MIN_CURSOR_DIAMETER) + getPaddingTop() + getPaddingBottom();

        setMinimumHeight(minHeight );
        setMinimumWidth(minWidth);

    }



    /**
     * Wrapper passage DIP en pixels
     * @param valueInDp dimension en DIP
     * @return dimension en pixels
     */
    private float dpToPixel(float valueInDp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, getResources().getDisplayMetrics());
    }



    /**
     *  Conversion d'une valeur du Slider en une position de la poignée en pixels
     * @param value : valeur à convertir
     * @return : point associé au centre de la poignée
     */
    private Point toPos(float value) {
        int x, y;
        y = (int) ((1 - valueToRatio(value)) * mBarLength + mCursorDiameter / 2);
        x = (int) (Math.max(mCursorDiameter, mBarWidth) / 2);
        x += getPaddingLeft();
        y += getPaddingTop();

        return new Point(x, y);
    }



    /**
     * Normalisation de la valeur entre 0 et 1.
     * @param value
     * @return
     */
    private float valueToRatio(float value){

        return (value - mMin) / (mMax - mMin);
    }



    /**
     * Dénormalisation vers la valeur du curseur
     * @param ratio : valeur entre 0 (min) et 1 (max)
     * @return valeur associée au Slider
     */
    private float ratioToValue(float ratio) {
        return ratio * (mMax - mMin) + mMin;
    }



    /**
     * Convertions d'une position écran en une valeur du Slider
     * @param position : position écran
     * @return valeur slider
     */
    private float toValue(Point position){
        float ratio = 1 - (position.y -getPaddingTop()-mCursorDiameter/2)/mBarLength;
        if(ratio < 0) ratio = 0;
        if(ratio > 1) ratio = 1;
        return ratioToValue(ratio);

    }



    /**
     * Redéfinition de la méthode de tracé du Slider
     * @param canvas : zone de tracé
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Point p1, p2;
        p1 = toPos(mMin);
        p2 = toPos(mMax);

        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mBarPaint);

        // positionnement du curseur et de la barre d'amplitude
        Point cursorPosition = toPos(mValue);
        Point originPosition = toPos(Math.max(0, mMin));

        canvas.drawCircle(cursorPosition.x, cursorPosition.y, mCursorDiameter / 2, mCursorPaint);
    }

    /**
     * Redéfinition de la méthode de mesure du Slider
     * @param widthMeasureSpec : spécifications de largeur
     * @param heightMeasureSpec : spécifications de hauteur
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.i("MEASURE", MeasureSpec.toString(widthMeasureSpec));
        Log.i("MEASURE", MeasureSpec.toString(heightMeasureSpec));


        int suggestedWidth, suggestedHeight, width, height;



        // la dimension souhaitée est ajustée pour au moins atteindre
        // la dimension minimale acceptable
        suggestedWidth = Math.max(getSuggestedMinimumWidth(), (int) Math.max(mCursorDiameter, mBarWidth) + getPaddingLeft() + getPaddingRight());
        suggestedHeight = Math.max(getSuggestedMinimumHeight(), (int) (mBarLength + mCursorDiameter) + getPaddingTop() + getPaddingBottom());
        width = resolveSize(suggestedWidth, widthMeasureSpec);
        height = resolveSize(suggestedHeight, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }


    /*************************************************
     * Interface
     ************************************************/


    private SliderChangeListener mSliderChangeListener; /**< Référence vers le listener*/

    /**
     * Setter de listener
     * @param sliderChangeListener
     */
    public void setSliderChangeListener(SliderChangeListener sliderChangeListener){
        mSliderChangeListener = sliderChangeListener;
    }

    /**
     * Interface Listener
     */
    public interface SliderChangeListener{
        void onChange(float value);
        void onDoubleClick(float value);
    }



    /*************************************************
     * ACTIONS
     ************************************************/

    /**
     * Redéfinition de la gestion des actions
     * @param event : type d'action et paramètres associés
     * @return : indique que le gestionnaire a consommé l'évènement
     * sinon transfert de l'évènement aux éventuels Views en overlap
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()) {
            case MotionEvent.ACTION_MOVE :
                if(! moveActionDisabled) {
                    mSliderChangeListener.onChange(mValue);
                    mValue = toValue(new Point((int) event.getX(), (int) event.getY()));
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_DOWN :
                moveActionDisabled = true;
                if(isDoubleClick){
                    mValue = mMin;
                    mSliderChangeListener.onDoubleClick(mValue);
                    invalidate();
                } else
                    isDoubleClick = true;

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moveActionDisabled = false;
                    }
                },100);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    isDoubleClick = false;
                    }
                },300);
                break;
        }
        return true;
    }
}
