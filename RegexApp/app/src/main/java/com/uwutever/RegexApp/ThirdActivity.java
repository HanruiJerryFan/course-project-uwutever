package com.uwutever.RegexApp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uwutever.RegexApp.utils.controllers.MatchController;

import net.xqhs.graphs.graph.Node;
import net.xqhs.graphs.graph.SimpleEdge;
import net.xqhs.graphs.graph.SimpleNode;

import java.util.ArrayList;
import java.util.List;

import giwi.org.networkgraph.GraphSurfaceView;
import giwi.org.networkgraph.beans.NetworkGraph;
import giwi.org.networkgraph.beans.Vertex;

import com.uwutever.RegexApp.utils.controllers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ThirdActivity: Show the result of the regex match.
 *
 * 
 * @author HanruiJerryFan, RealFakeAccount
 * @version 1.0
 * @since 1.0
 */

public class ThirdActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.example.android.Regex.REPLY";
    private String RegexStr;
    private String SampleText;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        * Set the layout of the activity.
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        Intent intent = getIntent();

        RegexStr = intent.getStringExtra(SecondActivity.EXTRA_REGEX);
        SampleText = intent.getStringExtra(SecondActivity.EXTRA_SAMPLE);

        SearchController searchController = new SearchController(RegexStr,false);
        List<List<Integer>> matchedIntervals = searchController.search(SampleText);
        Log.d(TAG, "what?");
        Boolean result = !(matchedIntervals.size() == 0);

        if (result){
            message = highlight_MatchedPattern(matchedIntervals);
        }
        else {
            message = "No Match Pattern Available!";
        }

        TextView Matched_Pattern = findViewById(R.id.Text_MatchedPattern);
        Matched_Pattern.setText(Html.fromHtml(message));

        initialize_Visualization_NFA();

       final Button button = findViewById(R.id.Act3_button_save);
       button.setOnClickListener(new View.OnClickListener() {
           /**
            * Save the result of the regex match.
            *
            * @param view
            */
           public void onClick(View view) {
               Intent replyIntent = new Intent();
               if (TextUtils.isEmpty(RegexStr)) {
                   setResult(RESULT_CANCELED, replyIntent);
               } else {
                   String word = RegexStr;
                   replyIntent.putExtra(EXTRA_REPLY, word);
                   setResult(RESULT_OK, replyIntent);
               }
               finish();
           }
       });
    }

    /**
     * Return a DFAGraphData class
     */
    public DFAGraphData getDFAVisualization(String regex){
        DFAGrapher dfaGrapher = new DFAGrapher(regex);
        return dfaGrapher.graph();
    }

    private String highlight_MatchedPattern(List<List<Integer>> matchedintervals){
        String temp = "";
        int end = 0;
        Log.d(TAG, ((Integer) matchedintervals.size()).toString());
        for(int i = 0; i < matchedintervals.size(); i++){
            Log.d(TAG, matchedintervals.get(i).get(0).toString()+matchedintervals.get(i).get(1).toString());

            temp = temp + SampleText.substring(end, matchedintervals.get(i).get(0)) + "<font color='#226AD0'>"+ SampleText.substring(matchedintervals.get(i).get(0), matchedintervals.get(i).get(1)) + "</font>";
            end = matchedintervals.get(i).get(1);
        }
        return temp + SampleText.substring(end, SampleText.length());
    }

    /**
     * Return an NFAGraphData class
     */
    public NFAGraphData getNFAVisualization(String regex){
        NFAGrapher nfaGrapher = new NFAGrapher(regex);
        return nfaGrapher.graph();
    }

    public String StringToIntString(String str) {
        int ans = 0;
        while (!str.isEmpty()) {
            ans += str.charAt(str.length() - 1) - '0';
            ans *= 10;
            str = str.substring(0, str.length() - 1);
        }
        ans /= 10;
        ans %= 64;
        return String.valueOf(ans);
    }

    private void initialize_Visualization_DFA() {

        DFAGraphData dfa = getDFAVisualization(RegexStr); // create DFA data object based on regex

        NetworkGraph graph = new NetworkGraph();
        Map<String, Node> NodeMap = new HashMap<>();

        Log.d(TAG, String.valueOf(dfa.acceptingState.size()));

        for (Map.Entry<String, Map<String, String>> entry: dfa.transitionTable.entrySet()) { // add node into NodeMap
            String CurNodeName = StringToIntString(entry.getKey());

            if(! NodeMap.containsKey(CurNodeName)) {
                Node CurNode = new SimpleNode(CurNodeName);
                NodeMap.put(CurNodeName, CurNode);
            }
        }

        for (Map.Entry<String, Map<String, String>> entry: dfa.transitionTable.entrySet()) {
            Map<String, String> NextNodeData = entry.getValue();

            for (Map.Entry<String, String> entry1: NextNodeData.entrySet()) {
                String CurNodeName = StringToIntString(entry1.getValue());

                if(! NodeMap.containsKey(CurNodeName)) {
                    Node CurNode = new SimpleNode(CurNodeName);
                    NodeMap.put(CurNodeName, CurNode);
                }
            }
        }

        for (Map.Entry<String, Node> entry: NodeMap.entrySet()) { // add node into graph
            Node CurNode = entry.getValue();
            graph.getVertex().add(new Vertex(CurNode, ContextCompat.getDrawable(this, R.drawable.smallpixel)));
        }

        for (Map.Entry<String, Map<String, String>> entry: dfa.transitionTable.entrySet()) { // add edge
            String CurNodeName = StringToIntString(entry.getKey());

            Map<String, String> NextNodeData = entry.getValue();

            Node CurNode = NodeMap.get(CurNodeName);

            for (Map.Entry<String, String> entry1: NextNodeData.entrySet()) {
                String NextNodeName = StringToIntString(entry1.getValue());
                Node NextNode = NodeMap.get(NextNodeName);

                String EdgeLabel = StringToIntString(entry1.getKey());

                graph.addEdge(new SimpleEdge(CurNode, NextNode, EdgeLabel));
            }
        }

        GraphSurfaceView surface = (GraphSurfaceView) findViewById(R.id.visualization);
        surface.init(graph);
    }


    private void initialize_Visualization_NFA() {

        NFAGraphData nfa = getNFAVisualization(RegexStr); // create DFA data object based on regex

        NetworkGraph graph = new NetworkGraph();
        Map<String, Node> NodeMap = new HashMap<>();


        for (Map.Entry<String, Map<String, Set<String>>> entry: nfa.transitionTable.entrySet()) { // add node into NodeMap
            String CurNodeName = StringToIntString(entry.getKey());

            if(! NodeMap.containsKey(CurNodeName)) {
                Node CurNode = new SimpleNode(CurNodeName);
                NodeMap.put(CurNodeName, CurNode);
                Log.d(TAG, "Add Node:" + CurNodeName);
                graph.getVertex().add(new Vertex(CurNode, ContextCompat.getDrawable(this, R.drawable.smallpixel)));
            }
        }

        for (Map.Entry<String, Map<String, Set<String>>> entry: nfa.transitionTable.entrySet()) {// add node into NodeMap
            Map<String, Set<String>> NextNodeData = entry.getValue();

            for (Map.Entry<String, Set<String>> entry1: NextNodeData.entrySet()) {
                Set<String> NextNodeSet = entry1.getValue();

                for (String s: NextNodeSet) {
                    String CurNodeName = StringToIntString(s);

                    if(! NodeMap.containsKey(CurNodeName)) {
                        Node CurNode = new SimpleNode(CurNodeName);
                        NodeMap.put(CurNodeName, CurNode);
                        Log.d(TAG, "Add Node:" + CurNodeName);
                        graph.getVertex().add(new Vertex(CurNode, ContextCompat.getDrawable(this, R.drawable.smallpixel)));
                    }
                }

            }
        }

        int cnt = 0;
        for (Map.Entry<String, Map<String, Set<String>>> entry: nfa.transitionTable.entrySet()) { // add edge
            String CurNodeName = StringToIntString(entry.getKey());

            Map<String, Set<String>> NextNodeData = entry.getValue();

            Node CurNode = NodeMap.get(CurNodeName);

            for (Map.Entry<String, Set<String>> entry1: NextNodeData.entrySet()) {
                for (String s: entry1.getValue()) {
                    String NextNodeName = StringToIntString(s);
                    Node NextNode = NodeMap.get(NextNodeName);

                    String EdgeLabel = StringToIntString(entry1.getKey());
                    if (EdgeLabel.length() > 1) EdgeLabel = EdgeLabel.substring(0, 1);
                    ++cnt;
                    graph.addEdge(new SimpleEdge(CurNode, NextNode, EdgeLabel));
                    Log.d(TAG, "Add Edge: " + CurNodeName + " to " + NextNodeName + " label " + EdgeLabel);
                }
            }
        }

        Log.d(TAG, "Number of Edges:" + cnt);
        GraphSurfaceView surface = (GraphSurfaceView) findViewById(R.id.visualization);
        surface.init(graph);
    }

    private void initialize_Visualization_test() {
        NetworkGraph graph = new NetworkGraph();

        Node v0 = new SimpleNode("0");
        Node v1 = new SimpleNode("1");
        Node v2 = new SimpleNode("2");


        graph.getVertex().add(new Vertex(v0, ContextCompat.getDrawable(this, R.drawable.smallpixel)));
        graph.getVertex().add(new Vertex(v1, ContextCompat.getDrawable(this, R.drawable.smallpixel)));
        graph.getVertex().add(new Vertex(v2, ContextCompat.getDrawable(this, R.drawable.smallpixel)));

        graph.addEdge(new SimpleEdge(v0, v1, "1"));
        graph.addEdge(new SimpleEdge(v1, v2, "2"));
        graph.addEdge(new SimpleEdge(v2, v0, "3"));

        GraphSurfaceView surface = (GraphSurfaceView) findViewById(R.id.visualization);
        surface.init(graph);
    }

//    public void CallBackMainActivity(View view){
//
////        String RegexStr, SampleText;
//
////        if(bundle != null){
////            RegexStr = (String) bundle.get("RegexStr");
////            SampleText = (String) bundle.get("SampleText");
////        }
//
//        // jump to main activity
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtras(getIntent().getExtras()); // send Regex pattern and sample text to main activity
//        startActivity(intent);
//    }
}