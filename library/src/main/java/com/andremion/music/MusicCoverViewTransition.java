/*
 * Copyright (c) 2016. André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andremion.music;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.Property;
import android.view.ViewGroup;

import com.andremion.music.cover.R;

import java.util.ArrayList;
import java.util.List;

public class MusicCoverViewTransition extends Transition {

    private static final String PROPNAME_RADIUS = MusicCoverViewTransition.class.getName() + ":radius";
    private static final String PROPNAME_ALPHA = MusicCoverViewTransition.class.getName() + ":alpha";
    private static final String[] sTransitionProperties = {PROPNAME_RADIUS, PROPNAME_ALPHA};

    private static final Property<MusicCoverView, Float> RADIUS_PROPERTY =
            new Property<MusicCoverView, Float>(Float.class, "radius") {
                @Override
                public void set(MusicCoverView view, Float radius) {
                    view.setTransitionRadius(radius);
                }

                @Override
                public Float get(MusicCoverView view) {
                    return view.getTransitionRadius();
                }
            };

    private static final Property<MusicCoverView, Integer> ALPHA_PROPERTY =
            new Property<MusicCoverView, Integer>(Integer.class, "alpha") {
                @Override
                public void set(MusicCoverView view, Integer alpha) {
                    view.setTransitionAlpha(alpha);
                }

                @Override
                public Integer get(MusicCoverView view) {
                    return view.getTransitionAlpha();
                }
            };

    private final int mStartShape;

    public MusicCoverViewTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MusicCoverView);
        int shape = a.getInt(R.styleable.MusicCoverView_shape, MusicCoverView.SHAPE_RECTANGLE);
        a.recycle();
        mStartShape = shape;
    }

    public MusicCoverViewTransition(int shape) {
        mStartShape = shape;
    }

    @Override
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        // Add fake value to force calling of createAnimator method
        captureValues(transitionValues, "start");
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        // Add fake value to force calling of createAnimator method
        captureValues(transitionValues, "end");
    }

    private void captureValues(TransitionValues transitionValues, Object value) {
        if (transitionValues.view instanceof MusicCoverView) {
            transitionValues.values.put(PROPNAME_RADIUS, value);
            transitionValues.values.put(PROPNAME_ALPHA, value);
        }
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {

        if (endValues == null || !(endValues.view instanceof MusicCoverView)) {
            return null;
        }

        MusicCoverView coverView = (MusicCoverView) endValues.view;
        final float minRadius = coverView.getMinRadius();
        final float maxRadius = coverView.getMaxRadius();

        float startRadius, endRadius;
        int startTrackAlpha, endTrackAlpha;

        if (mStartShape == MusicCoverView.SHAPE_RECTANGLE) {
            startRadius = maxRadius;
            endRadius = minRadius;
            startTrackAlpha = MusicCoverView.ALPHA_TRANSPARENT;
            endTrackAlpha = MusicCoverView.ALPHA_OPAQUE;
        } else {
            startRadius = minRadius;
            endRadius = maxRadius;
            startTrackAlpha = MusicCoverView.ALPHA_OPAQUE;
            endTrackAlpha = MusicCoverView.ALPHA_TRANSPARENT;
        }

        List<Animator> animatorList = new ArrayList<>();

        coverView.setTransitionRadius(startRadius);
        animatorList.add(ObjectAnimator.ofFloat(coverView, RADIUS_PROPERTY, startRadius, endRadius));

        coverView.setTransitionAlpha(startTrackAlpha);
        animatorList.add(ObjectAnimator.ofInt(coverView, ALPHA_PROPERTY, startTrackAlpha, endTrackAlpha));

        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(animatorList);
        return animator;
    }

}
