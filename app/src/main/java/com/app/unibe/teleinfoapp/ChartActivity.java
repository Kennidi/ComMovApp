package com.app.unibe.teleinfoapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ComboLineColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

public class ChartActivity extends AppCompatActivity {

    private TelephonyManager tm;
    private MyPhoneStateListener pslistener;
    private static int valor = 0;
    private PlaceholderFragment placeholderFragment;
    private static TextView lbSenal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        lbSenal = (TextView) findViewById(R.id.lbSenal);


        placeholderFragment = new PlaceholderFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, placeholderFragment).commit();
        }

        pslistener = new MyPhoneStateListener();
        tm = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(pslistener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    /**
     * A fragment containing a combo line/column chart view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ComboLineColumnChartView chart;
        private ComboLineColumnChartData data;

        private int numberOfLines = 0;
        private int maxNumberOfLines = 0;
        private int numberOfPoints = 0;

        float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

        private boolean hasAxes = false;
        private boolean hasAxesNames = false;
        private boolean hasPoints = false;
        private boolean hasLines = false;
        private boolean isCubic = false;
        private boolean hasLabels = false;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_combo_line_column_chart, container, false);

            chart = (ComboLineColumnChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());

            generateData();

            return rootView;
        }

        // MENU
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.combo_line_column_chart, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_reset) {
                reset();
                generateData();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        private void reset() {
            numberOfLines = 1;

            hasAxes = false;
            hasAxesNames = false;
            hasLines = false;
            hasPoints = false;
            hasLabels = false;
            isCubic = false;

        }

        public void generateData() {
            // Chart looks the best when line data and column data have similar maximum viewports.
            data = new ComboLineColumnChartData(generateColumnData(), generateLineData());

            if (hasAxes) {
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Señal");
                    axisY.setName("Intensidad (-dB)");
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            chart.setComboLineColumnChartData(data);
        }

        private LineChartData generateLineData() {

            List<Line> lines = new ArrayList<Line>();
            for (int i = 0; i < numberOfLines; ++i) {

                //Point "y" values
                List<PointValue> values = new ArrayList<PointValue>();
                for (int j = 0; j < numberOfPoints; ++j) {
                    values.add(new PointValue(j, randomNumbersTab[i][j]));
                }

                Line line = new Line(values);
                line.setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
                line.setCubic(isCubic);
                line.setHasLabels(true);
                line.setHasLines(true);
                line.setHasPoints(false);
                lines.add(line);
            }

            LineChartData lineChartData = new LineChartData(lines);

            return lineChartData;

        }

        private ColumnChartData generateColumnData() {

            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;

            if(valor < -112){
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(10, Color.rgb(239, 51, 64)));
                columns.add(new Column(values));

                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(30, ChartUtils.DEFAULT_COLOR));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(50, ChartUtils.DEFAULT_COLOR));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(70, ChartUtils.DEFAULT_COLOR));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(90, ChartUtils.DEFAULT_COLOR));
                columns.add(new Column(values));

                lbSenal.setTextColor(Color.rgb(239, 51, 64));
            }

            if(valor > -113 && valor < -100){
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(10, Color.rgb(227, 232, 41)));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(30, Color.rgb(227, 232, 41)));
                columns.add(new Column(values));

                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(50, ChartUtils.DEFAULT_COLOR));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(70, ChartUtils.DEFAULT_COLOR));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(90, ChartUtils.DEFAULT_COLOR));
                columns.add(new Column(values));

                lbSenal.setTextColor(Color.rgb(227, 232, 41));
            }

            if(valor > -101 && valor < -90){
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(10, Color.rgb(227, 232, 41)));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(30, Color.rgb(227, 232, 41)));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(50, Color.rgb(227, 232, 41)));
                columns.add(new Column(values));

                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(70, ChartUtils.DEFAULT_COLOR));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(90, ChartUtils.DEFAULT_COLOR));
                columns.add(new Column(values));

                lbSenal.setTextColor(Color.rgb(227, 232, 41));
            }

            if(valor > -91 && valor < -51){
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(10, Color.rgb(0, 171, 132)));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(30, Color.rgb(0, 171, 132)));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(50, Color.rgb(0, 171, 132)));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(70, Color.rgb(0, 171, 132)));
                columns.add(new Column(values));

                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(90, ChartUtils.DEFAULT_COLOR));
                columns.add(new Column(values));

                lbSenal.setTextColor(Color.rgb(0, 171, 132));
            }

            if(valor > -52){
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(10, Color.rgb(0, 171, 132)));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(30, Color.rgb(0, 171, 132)));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(50, Color.rgb(0, 171, 132)));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(70, Color.rgb(0, 171, 132)));
                columns.add(new Column(values));
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(90, Color.rgb(0, 171, 132)));
                columns.add(new Column(values));

                lbSenal.setTextColor(Color.rgb(0, 171, 132));
            }

            ColumnChartData columnChartData = new ColumnChartData(columns);
            return columnChartData;
        }

        //ColumnChartOnValueSelect actions
        private class ValueTouchListener implements ComboLineColumnChartOnValueSelectListener {

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onColumnValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                String res = "";
                if(value.getValue() == 10){
                    res = "Débil (menor a -112)";
                }else if(value.getValue() == 30){
                    res = "Moderada (entre -113 y -101)";
                }else if(value.getValue() == 50){
                    res = "Buena (entre -102 y -90)";
                }else if(value.getValue() == 70){
                    res = "Fuerte (entre -91 y -51)";
                }else{
                    res = "Excelente (mayor a -52)";
                }
                Toast.makeText(getActivity(), "Señal " + res, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPointValueSelected(int lineIndex, int pointIndex, PointValue value) {
                //Toast.makeText(getActivity(), "Selected line point: " + value, Toast.LENGTH_SHORT).show();
            }

        }
    }

    /* Called when the application is minimized */
    @Override
    protected void onPause()
    {
        super.onPause();
        tm.listen(pslistener, PhoneStateListener.LISTEN_NONE);
    }

    /* Called when the application resumes */
    @Override
    protected void onResume()
    {
        super.onResume();
        tm.listen(pslistener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    //PhoneStateListener class
    private class MyPhoneStateListener extends PhoneStateListener
    {
        /* Get the Signal strength from the provider, each tiome there is an update */
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength)
        {
            super.onSignalStrengthsChanged(signalStrength);

            try{
                valor = (2 * signalStrength.getGsmSignalStrength()) - 113;

                lbSenal.setText("["+ String.valueOf(valor) +" dB]");

                placeholderFragment.generateData();

            }catch (Exception ex){
                //do nothing
            }

        }

    }

}
