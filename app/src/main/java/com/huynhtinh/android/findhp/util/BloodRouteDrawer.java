package com.huynhtinh.android.findhp.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huynhtinh1997 on 04/12/2017.
 */

public class BloodRouteDrawer {

    private Polyline backgroundPolyline;
    private Polyline foregroundPolyline;

    private AnimatorSet firstRunAnimSet;
    private AnimatorSet secondLoopRunAnimSet;

    private ValueAnimator mDrawBackgroundRouteAnimator;
    private ValueAnimator mDrawForegroundRouteAnimator;
    private ValueAnimator mColorForegroundRouteAnimator;

    private List<LatLng> mMainRoute = new ArrayList<>();
    private List<LatLng> mBackgroundTmpRoute = new ArrayList<>();
    private List<LatLng> mForegroundTmpRoute = new ArrayList<>();

    private int mForegroundColor;
    private int mBackgroundColor;

    public void animateRoute(GoogleMap googleMap, final List<LatLng> route,
                             final int backgroundColor, final int foregroundColor,
                             final int width) {
        clearAnimate();

        PolylineOptions optionsBackground = new PolylineOptions()
                .add(route.get(0))
                .color(backgroundColor)
                .width(width);
        backgroundPolyline = googleMap.addPolyline(optionsBackground);

        PolylineOptions optionsForeground = new PolylineOptions()
                .add(route.get(0))
                .color(foregroundColor)
                .width(width);
        foregroundPolyline = googleMap.addPolyline(optionsForeground);

        mMainRoute = route;

        mForegroundColor = foregroundColor;
        mBackgroundColor = backgroundColor;

        initDrawBackgroundRouteAnimation();
        initDrawForegroundRouteAnimation();
        initColorForegroundAnimation();

        firstRunAnimSet = new AnimatorSet();
        secondLoopRunAnimSet = new AnimatorSet();

        initAndPlayFirstRunAnimSet();

    }


    private void initDrawBackgroundRouteAnimation() {
        mDrawBackgroundRouteAnimator = ValueAnimator.ofInt(0, mMainRoute.size() - 1);
        mDrawBackgroundRouteAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mBackgroundTmpRoute.add(mMainRoute.get((Integer) valueAnimator.getAnimatedValue()));
                backgroundPolyline.setPoints(mBackgroundTmpRoute);
            }
        });
        mDrawBackgroundRouteAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                backgroundPolyline.setPoints(mMainRoute);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        mDrawBackgroundRouteAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mDrawBackgroundRouteAnimator.setDuration(1600);
    }

    private void initDrawForegroundRouteAnimation() {
        mDrawForegroundRouteAnimator = ValueAnimator.ofInt(0, mMainRoute.size() - 1);
        mDrawForegroundRouteAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mForegroundTmpRoute.add(mMainRoute.get((Integer) valueAnimator.getAnimatedValue()));
                foregroundPolyline.setPoints(mForegroundTmpRoute);
            }
        });
        mDrawForegroundRouteAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                foregroundPolyline.setPoints(mMainRoute);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        mDrawForegroundRouteAnimator.setInterpolator(new DecelerateInterpolator());
        mDrawForegroundRouteAnimator.setDuration(2000);
    }

    private void initColorForegroundAnimation() {
        mColorForegroundRouteAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), mForegroundColor, mBackgroundColor);
        mColorForegroundRouteAnimator.setInterpolator(new AccelerateInterpolator());
        mColorForegroundRouteAnimator.setDuration(1200);

        mColorForegroundRouteAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                foregroundPolyline.setColor((int) animator.getAnimatedValue());
            }
        });

        mColorForegroundRouteAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mForegroundTmpRoute.clear();
                foregroundPolyline.setPoints(mForegroundTmpRoute);
                foregroundPolyline.setColor(mForegroundColor);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void initAndPlayFirstRunAnimSet() {
        firstRunAnimSet.playSequentially(mDrawBackgroundRouteAnimator, mDrawForegroundRouteAnimator);
        firstRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                initAndSecondLoopAnimSet();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        firstRunAnimSet.start();
    }

    private void initAndSecondLoopAnimSet() {
        secondLoopRunAnimSet.playSequentially(mColorForegroundRouteAnimator, mDrawForegroundRouteAnimator);
        secondLoopRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                secondLoopRunAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        secondLoopRunAnimSet.setStartDelay(200);
        secondLoopRunAnimSet.start();
    }


    public void clearAnimate() {
        clearAnimatorSet(firstRunAnimSet);
        clearAnimatorSet(secondLoopRunAnimSet);

        clearValueAnimator(mDrawForegroundRouteAnimator);
        clearValueAnimator(mDrawBackgroundRouteAnimator);
        clearValueAnimator(mColorForegroundRouteAnimator);

        //Reset the polylines
        if (foregroundPolyline != null) foregroundPolyline.remove();
        if (backgroundPolyline != null) backgroundPolyline.remove();

        mMainRoute.clear();
        mBackgroundTmpRoute.clear();
        mForegroundTmpRoute.clear();

    }

    private void clearAnimatorSet(AnimatorSet animatorSet) {
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            animatorSet.end();
            animatorSet.cancel();
        }
    }

    private void clearValueAnimator(ValueAnimator animator) {
        if (animator != null) {
            animator.removeAllUpdateListeners();
            animator.removeAllListeners();
            animator.end();
            animator.cancel();
        }
    }

}
