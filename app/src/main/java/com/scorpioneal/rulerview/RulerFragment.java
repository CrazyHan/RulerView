package com.scorpioneal.rulerview;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ScorpioNeal on 15/7/29.
 */
public class RulerFragment extends Fragment {

    private TextView mBirthTV, mHeightTV, mWeightTv;
    private RulerView mBirthView, mHeightView, mWeightView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ruler_layout, container, false);
        mBirthTV = (TextView)view.findViewById(R.id.birth_tv);
        mHeightTV = (TextView)view.findViewById(R.id.height_tv);
        mWeightTv = (TextView)view.findViewById(R.id.weight_tv);
        mBirthView = (RulerView)view.findViewById(R.id.birthRulerView);
        mHeightView = (RulerView)view.findViewById(R.id.heightRulerView);
        mWeightView = (RulerView)view.findViewById(R.id.weightRulerView);

        mBirthView.setmStartValue(0);
        mBirthView.setmEndValue(10000);
        mBirthView.setmOriginValue(2000);
        mBirthView.setOriginValueSmall(0);
        mBirthView.setmPartitionWidthInDP(106.7f);
        mBirthView.setmPartitionValue(1000);
        mBirthView.setmSmallPartitionCount(1);
        mBirthView.setmValue(1990);
        mBirthView.setValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int intVal, int fltval) {
                mBirthTV.setText(intVal + " " + fltval);
            }
        });

        mHeightView.setmStartValue(50);
        mHeightView.setmEndValue(250);
        mHeightView.setmPartitionWidthInDP(40);
        mHeightView.setmPartitionValue(1);
        mHeightView.setmSmallPartitionCount(1);
        mHeightView.setmValue(170);
        mHeightView.setValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int intVal, int fltval) {
                mHeightTV.setText(intVal + " " + fltval);
            }
        });

        mWeightView.setmStartValue(20);
        mWeightView.setmEndValue(250);
        mWeightView.setmPartitionWidthInDP(36.7f);
        mWeightView.setmPartitionValue(1);
        mWeightView.setmSmallPartitionCount(2);
        mWeightView.setmValue(106);
        mWeightView.setOriginValueSmall(1);
        mWeightView.setValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int intVal, int fltval) {
                mWeightTv.setText(intVal + " " + fltval);
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
