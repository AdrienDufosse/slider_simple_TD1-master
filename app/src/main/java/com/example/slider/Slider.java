package com.example.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class Slider extends View {

    // dimensions minimales du widget (en dp) : uniquement les éléments critiques
    final static float MIN_BAR_LENGTH = 160;
    final static float MIN_CURSOR_DIAMETER = 30;

    // dimensions par défaut du widget (en dp)
    final static float DEFAULT_BAR_WIDTH = 20;
    final static float DEFAULT_BAR_LENGTH = 160;
    final static float DEFAULT_CURSOR_DIAMETER = 40;


    // définition des pinceaux
    private Paint mCursorPaint = null;
    private Paint mValueBarPaint = null;
    private Paint mBarPaint = null;

    // coloris du Slider
    private int mDisabledColor;
    private int mCursorColor;
    private int mBarColor;
    private int mValueBarColor;

    // définition des dimensions (en pixel)
    private float mBarLength;
    private float mBarWidth;
    private float mCursorDiameter;

    private boolean mEnabled = true;

    // Valeur du Slider
    private float mValue = 50;
    // Borne min
    private float mMin = 0;
    // Borne max
    private float mMax = 100;

    //Listener
    private SliderChangeListener mSliderChangeListener;


    /**
     * Constructeur dynamique
     *
     * @param context
     */
    public Slider(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Constructeur statique
     *
     * @param context
     * @param attributeSet
     */
    public Slider(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }


    /**
     * Initialisation des éléments du Slider afin d'éviter une surcharge de calcul lors d'une mise à jour graphique
     *
     * @param context      : le contexte dans lequel le slider est utilisé
     * @param attributeSet : les attributs définis dans le fichier XML
     */
    private void init(Context context, AttributeSet attributeSet) {

        // Initialisation des dimensions minimales en pixel
        mBarLength = dpToPixel(DEFAULT_BAR_LENGTH);
        mCursorDiameter = dpToPixel(DEFAULT_CURSOR_DIAMETER);
        mBarWidth = dpToPixel(DEFAULT_BAR_WIDTH);

        // intanciation des paints (par défaut)
        mCursorPaint = new Paint();
        mBarPaint = new Paint();
        mValueBarPaint = new Paint();

        // suppression du repliement
        mCursorPaint.setAntiAlias(true);
        mBarPaint.setAntiAlias(true);
        mValueBarPaint.setAntiAlias(true);

        // Application du style (plein)
        mValueBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setStyle(Paint.Style.STROKE);
        mCursorPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // Spécification des terminaisons
        mBarPaint.setStrokeCap(Paint.Cap.ROUND);
        mValueBarPaint.setStrokeCap(Paint.Cap.ROUND);

        // default colors
        mDisabledColor = ContextCompat.getColor(context, R.color.colorDisabled);
        mCursorColor = ContextCompat.getColor(context, R.color.colorAccent);
        mBarColor = ContextCompat.getColor(context, R.color.colorPrimary);
        mValueBarColor = ContextCompat.getColor(context, R.color.colorSecondary);

        // et finalement des couleurs (utilisation des couleurs du thème par défaut)
        if (mEnabled) {
            mCursorPaint.setColor(mCursorColor);
            mBarPaint.setColor(mBarColor);
            mValueBarPaint.setColor(mValueBarColor);
        } else {
            mCursorPaint.setColor(mDisabledColor);
            mBarPaint.setColor(mDisabledColor);
            mValueBarPaint.setColor(mDisabledColor);
        }

        // fixe les  largeurs
        mBarPaint.setStrokeWidth(mBarWidth);
        mValueBarPaint.setStrokeWidth(mBarWidth);

        // initialisation des dimensions minimales
        int minWidth = (int) dpToPixel(MIN_CURSOR_DIAMETER) + getPaddingTop() + getPaddingBottom();
        int minHeight = (int) dpToPixel(MIN_BAR_LENGTH + MIN_CURSOR_DIAMETER) + getPaddingLeft() + getPaddingRight();

        // fixe les dimensions minimales suggérées à Android pour ce slider
        // ces dimensions correspondent à la taille du canvas (objet + padding)
        setMinimumHeight(minHeight);
        setMinimumWidth(minWidth);
    }


    /**
     * Appelé lorsqu'Android crée la mise en page de l'activité contenant le slider
     *
     * @param widthMeasureSpec  : largeur spécifiée par Android pour le slider
     * @param heightMeasureSpec : hauteur spécifiée par Android
     *                          <p>
     *                          les valeurs spécifiées sont composites : une partie indique la dimension en pixel et
     *                          l'autre indique EXACTLY(dimension fixée), UNSPECIFIED(dimension laissée à la discrétion du slider) ou AT_MOST(dimension maxi)
     *                          <p>
     *                          onMeasure modifie les dimensions des éléments du Slider pour se conformer à l'espace alloué
     *                          par Android tout en conservant le padding.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.i("MEASURE", MeasureSpec.toString(widthMeasureSpec));
        Log.i("MEASURE", MeasureSpec.toString(heightMeasureSpec));

        int suggestedWidth, suggestedHeight, width, height;

        // la dimension souhaitée est ajustée pour au moins atteindre la dimension minimale acceptable
        suggestedWidth = Math.max(getSuggestedMinimumWidth(), (int) Math.max(mCursorDiameter, mBarWidth) + getPaddingLeft() + getPaddingRight());
        suggestedHeight = Math.max(getSuggestedMinimumHeight(), (int) (mBarLength + mCursorDiameter) + getPaddingTop() + getPaddingBottom());

        // cette méthode adapte la dimension demandée aux spécifications
        // Si la place est disponible, la suggestion est conservée sinon la contrainte est conservée
        width = resolveSize(suggestedWidth, widthMeasureSpec);
        height = resolveSize(suggestedHeight, heightMeasureSpec);

        setMeasuredDimension(width, height);

        //Initialisation de la valeur affichée à 50
        mSliderChangeListener.onChange(50);
    }


    /**
     * Redéfinition du tracé du widget
     *
     * @param canvas : surface de tracé
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // reprend le maximum d'espace sur le padding pour atteindre les tailles mini
        reconciliateDims();

        Point p1, p2;
        p1 = toPos(mMin);
        p2 = toPos(mMax);

        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mBarPaint);

        // positionnement du curseur et de la barre d'amplitude
        Point cursorPosition = toPos(mValue);
        Point originPosition = toPos(Math.max(0, mMin));

        if (!originPosition.equals(cursorPosition)) {
            canvas.drawLine(originPosition.x, originPosition.y, cursorPosition.x, cursorPosition.y, mValueBarPaint);
        }
        canvas.drawCircle(cursorPosition.x, cursorPosition.y, mCursorDiameter / 2, mCursorPaint);

    }


    /**
     * Méthode appelée au début de onDraw pour réduire les dimensions du slider ou du padding
     * afin de respecter l'espace alloué par le gestionnaire de layout. Si une réduction est opérée
     * le slider se retrouve centré horizontalement (rightPadding = leftPadding).
     */
    private void reconciliateDims() {

        float paddingLeft = getPaddingLeft();
        float paddingRight = getPaddingRight();
        float paddingTop = getPaddingTop();
        float paddingBottom = getPaddingBottom();
        float availableForPadding;

        // largeur insuffisante même sans padding
        if (getWidth() < dpToPixel(MIN_CURSOR_DIAMETER)) {
            mCursorDiameter = getWidth();
            paddingLeft = paddingRight = 0;
        }
        // largeur insuffisante avec padding mais suffisante sans
        else if (getWidth() - getPaddingLeft() - getPaddingRight() < dpToPixel(MIN_CURSOR_DIAMETER)) {
            mCursorDiameter = dpToPixel(MIN_CURSOR_DIAMETER);
            availableForPadding = getWidth() - mCursorDiameter;
            paddingLeft *= (availableForPadding / (getPaddingLeft() + getPaddingRight()));
            paddingRight *= (availableForPadding / (getPaddingLeft() + getPaddingRight()));
        }
        // largeur suffisante et curseur trop petit
        else if (mCursorDiameter < dpToPixel(MIN_CURSOR_DIAMETER)) {
            mCursorDiameter = dpToPixel(MIN_CURSOR_DIAMETER);
        }


        // adaptation de la largeur glissière pour être conforme.
        mBarWidth = Math.min(mBarWidth, getWidth() - paddingLeft - paddingRight);


        // hauteur insuffisante même sans padding
        if (getHeight() < mCursorDiameter + dpToPixel(MIN_BAR_LENGTH)) {
            mBarLength = Math.max(getHeight() - mCursorDiameter, 0);
            paddingBottom = paddingTop = 0;
        }

        // hauteur insuffisante avec padding mais suffisante sans
        else if (getHeight() - getPaddingTop() - getPaddingBottom() < dpToPixel(MIN_BAR_LENGTH)) {
            mBarLength = dpToPixel(MIN_BAR_LENGTH);
            availableForPadding = getHeight() - mBarLength;
            paddingTop *= (availableForPadding / (getPaddingTop() + getPaddingBottom()));
            paddingBottom *= (availableForPadding / (getPaddingTop() + getPaddingBottom()));
        } else if (mBarLength < dpToPixel(MIN_BAR_LENGTH)) {
            mBarLength = dpToPixel(MIN_BAR_LENGTH);
        }


        setPadding((int) paddingLeft, (int) paddingTop, (int) paddingRight, (int) paddingBottom);
    }


    /**
     * Conversion d'un position écran en valeur Slider
     *
     * @param position : position à l'écran
     * @return : valeur Slider
     */
    private float toValue(Point position) {
        float ratio = (position.y - getPaddingTop() - mCursorDiameter / 2) / mBarLength;
        if (ratio < 0) ratio = 0;
        if (ratio > 1) ratio = 1;
        return ratioToValue(1-ratio);
    }


    private Point toPos(float value) {
        int x, y, z;
        // à présent x1 et y1 représentent le centre du curseur
        y = (int) ((1 - valueToRatio(value)) * mBarLength + mCursorDiameter / 2);
        x = (int) (Math.max(mCursorDiameter, mBarWidth) / 2);
        x = x + getPaddingLeft();
        y = y + getPaddingTop();

        return new Point(x, y);
    }

    /**
     * Convertit une valeur en dp en pixels
     *
     * @param valueInDp : valeur à convertir
     * @return : nombre de pixels correspondant
     */
    private float dpToPixel(float valueInDp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, getResources().getDisplayMetrics());
    }


    /**
     * Transforme la valeur du slider en une proportion (utile pour positionner le curseur)
     *
     * @return le ratio du slider
     */
    private float valueToRatio(float value) {
        return (value - mMin) / (mMax - mMin);
    }


    /**
     * transforme une proportion en valeur.
     *
     * @param ratio : proportion
     * @return
     */
    private float ratioToValue(float ratio) {
        return ratio * (mMax - mMin) + mMin;
    }

    /*************************************************************************************/
    /*                                       Gestion des événements                      */

    /*************************************************************************************/

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                Point point = new Point((int) event.getX(), (int) event.getY());
                float newValue = toValue(point);
                setValue(newValue);
                mSliderChangeListener.onChange(newValue);
                break;
        }
        invalidate();
        return true;
    }

    public interface SliderChangeListener {
        public void onChange(float newValue);
    }

    /**
     *
     * @param sliderListener
     */
    public void setSliderChangeListener(SliderChangeListener sliderListener) {
        mSliderChangeListener = sliderListener;
    }

    /*************************************************************************************/
    /*                                       SETTERS et GETTERS                          */

    /*************************************************************************************/


    public float getValue() {
        return mValue;
    }


    /**
     * Fixe la valeur du Slider
     *
     * @param value : valeur entre min et max
     */
    public void setValue(float value) {
        mValue = value;
    }


    /**
     * Fixe la borne min du Slider
     *
     * @param min
     */
    public void setMin(float min) {
        mMin = min;
    }

    /**
     * Fixe la borne max du Slider
     *
     * @param max
     */
    public void setMax(float max) {
        mMax = max;
    }


    @Override
    public boolean isEnabled() {
        return mEnabled;

    }

    /**
     * Active le Slider
     */
    public void disable() {
        mEnabled = false;
        mCursorPaint.setColor(mDisabledColor);
        mBarPaint.setColor(mDisabledColor);
        mValueBarPaint.setColor(mDisabledColor);
        invalidate();
    }


    /**
     * Desactive le Slider, il n'es plus modifiable par l'utilisateur
     */
    public void enable() {
        mEnabled = true;
        mCursorPaint.setColor(mCursorColor);
        mBarPaint.setColor(mBarColor);
        mValueBarPaint.setColor(mValueBarColor);
        // force onDraw
        invalidate();
    }

    /*************************************************************************************/
    /*                        GESTION DE LA PERSISTANCE D'ETAT                           */
    /*************************************************************************************/

    /**
     * Méthode appelée lors d'un changement de configuration (Pour chaque View, cette méthode est
     * appelée afin de gérer la persistance en interne et alléger ainsi l'activité)
     * @return
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        // récupération des données déja agglomérées par ce View et ajout de mValue
        return new SavedState(super.onSaveInstanceState(), mValue);
    }

    /**
     * Appelé lors de la restitution de l'état
     * @param state instance de SavedState contenant l'ensemble des données à restituer
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        mValue = ((SavedState) state).sliderValue;
        super.onRestoreInstanceState(((SavedState) state).getSuperState());
        Log.i("RESTORE", "state restored with " + mValue);
    }

    /**
     * Cette classe interne stocke les états. Elle est Parcelable afin d'être persistée en mémoire interne
     */
    static class SavedState extends BaseSavedState {

        private float sliderValue;

        /**
         * L'objet Parcelable.Creator fournit une Factory permettant de reconstrure un objet à partir d'un Parcel
         */
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        /**
         * Constructeur privé utilisé par la factory pour restaurer l'objet à partir du Parcel
         * les données doivent être lues dans le parcel dans le même ordre que celui d'écriture
         *
         * @param source sérialisation des données sauvegardées
         */
        private SavedState(Parcel source) {
            // lecture par le View
            super(source);
            // lecture spécifique Slider
            sliderValue = source.readFloat();
        }

        /**
         * Constructeur utilisé lors de la sauvegarde de l'état
         *
         * @param superState : éléments spécifiques au View
         * @param value      : élément spécifique au slider
         */
        public SavedState(Parcelable superState, float value) {
            super(superState);
            sliderValue = value;
        }

        /**
         * Construction du Parcel par adjonction de value
         *
         * @param out   parcel
         * @param flags
         */
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(sliderValue);
        }
    }
}