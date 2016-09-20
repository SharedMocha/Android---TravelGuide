package com.travelguide.helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.travelguide.models.LeaderBoard;
import com.travelguide.models.MasterLeaderBoard;
import com.travelguide.models.Questions;

import java.util.List;

/**
 * Created by htammare on 8/26/2016.
 */

public class UpdatePointsandLeaderBoard extends AsyncTask<String, Void,Void> {
    // Async task to parse the downloaded results and build NPlace list

    @Override
    protected Void doInBackground(String... params) {

        final String questionId = params[0];
        final String selectedText = params[1];




        //Toast.makeText(getApplicationContext(), "Your answer "+selectedText+" is saved", Toast.LENGTH_SHORT).show();
        final ParseUser user = ParseUser.getCurrentUser();

        //fetch question details and save it in leader board
        ParseQuery<Questions> query = ParseQuery.getQuery(Questions.class);
        query.whereEqualTo("objectId",questionId);
        query.findInBackground(new FindCallback<Questions>() {
            @Override
            public void done(final List<Questions> questions, ParseException e) {
                if (e == null) {

                    LeaderBoard leaderBoard = new LeaderBoard();
                    leaderBoard.put("parentQuestionID", ParseObject.createWithoutData("Questions", questionId));
                    leaderBoard.put("userID", ParseObject.createWithoutData("_User",user.getObjectId()));
                    leaderBoard.puthuntID(questions.get(0).getHuntID());
                    leaderBoard.putlevelID(questions.get(0).getLevelID());
                    leaderBoard.putQuadrantNo(questions.get(0).getQuadrantNo());
                    leaderBoard.putQuestionDetails(questions.get(0).getQuestionDetails());
                    leaderBoard.putSelectedOption(selectedText);
                    leaderBoard.putQuestionNo(questions.get(0).getQuestionNo());
                    leaderBoard.putCorrectAnswer(questions.get(0).getCorrectOption());
                    if(selectedText.equals(questions.get(0).getCorrectOption().toString())) {
                        leaderBoard.putPoints(questions.get(0).getPoints());
                    }else{
                        leaderBoard.putPoints(0);
                    }
                    leaderBoard.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            ParseQuery<LeaderBoard> query = ParseQuery.getQuery(LeaderBoard.class);
                            query.whereEqualTo("huntID",questions.get(0).getHuntID());
                            query.whereEqualTo("levelID",questions.get(0).getLevelID());
                            query.findInBackground(new FindCallback<LeaderBoard>() {
                                @Override
                                public void done(List<LeaderBoard> list, ParseException e) {
                                    Integer totalLevelPoints = 0;
                                    for(int i=0;i<list.size();i++){
                                        totalLevelPoints = totalLevelPoints + list.get(i).getPoints();
                                    }
                                    for(int i=0;i<list.size();i++){
                                        LeaderBoard updateleaderBoard = list.get(i);
                                        updateleaderBoard.putTotalLevelPoints(totalLevelPoints);
                                        updateleaderBoard.putTotalHuntPoints(0);
                                        updateleaderBoard.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                ParseQuery<LeaderBoard> query = ParseQuery.getQuery(LeaderBoard.class);
                                                query.whereEqualTo("huntID",questions.get(0).getHuntID());
                                                query.whereNotEqualTo("points",0);
                                                query.findInBackground(new FindCallback<LeaderBoard>() {
                                                    @Override
                                                    public void done(List<LeaderBoard> list, ParseException e) {
                                                        Integer totalHuntPoints = 0;
                                                        final Integer huntPoints;
                                                        for(int i=0;i<list.size();i++){
                                                            totalHuntPoints = totalHuntPoints + list.get(i).getLevelPoints();
                                                        }
                                                        huntPoints = totalHuntPoints;
                                                        for(int i=0;i<list.size();i++){
                                                            LeaderBoard updateleaderBoard = list.get(i);
                                                            updateleaderBoard.putTotalHuntPoints(totalHuntPoints);
                                                            updateleaderBoard.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    ParseQuery<MasterLeaderBoard> query = ParseQuery.getQuery(MasterLeaderBoard.class);
                                                                    query.whereEqualTo("huntID",questions.get(0).getHuntID());
                                                                    query.findInBackground(new FindCallback<MasterLeaderBoard>() {
                                                                        @Override
                                                                        public void done(List<MasterLeaderBoard> list, ParseException e) {
                                                                            if(list != null) {


                                                                                if (list.size() > 0) {
                                                                                    MasterLeaderBoard updateRecord = list.get(0);
                                                                                    updateRecord.putPoints(huntPoints);
                                                                                    updateRecord.saveInBackground();

                                                                                        /*
                                                                                        for(int i=0;i<list.size();i++){

                                                                                            MasterLeaderBoard deleteRecord = list.get(i);
                                                                                            try {
                                                                                                deleteRecord.delete();
                                                                                                //deleteRecord.save();
                                                                                            } catch (ParseException e1) {
                                                                                                e1.printStackTrace();
                                                                                            }
                                                                                        }

                                                                                        MasterLeaderBoard saveNewRecord = new MasterLeaderBoard();
                                                                                        saveNewRecord.putPoints(huntPoints);
                                                                                       saveNewRecord.putHuntID(questions.get(0).getHuntID());
                                                                                        saveNewRecord.put("userID", ParseObject.createWithoutData("_User", user.getObjectId()));
                                                                                        try {
                                                                                            saveNewRecord.save();
                                                                                        } catch (ParseException e1) {
                                                                                            e1.printStackTrace();
                                                                                        }
                                                                                        */

                                                                                } else {
                                                                                    MasterLeaderBoard saveNewRecord = new MasterLeaderBoard();
                                                                                    saveNewRecord.putPoints(huntPoints);
                                                                                    saveNewRecord.putHuntID(questions.get(0).getHuntID());
                                                                                    saveNewRecord.put("userID", ParseObject.createWithoutData("_User", user.getObjectId()));
                                                                                    try {
                                                                                        saveNewRecord.save();
                                                                                    } catch (ParseException e1) {
                                                                                        e1.printStackTrace();
                                                                                    }
                                                                                }
                                                                            }else{
                                                                                MasterLeaderBoard saveNewRecord = new MasterLeaderBoard();
                                                                                saveNewRecord.putPoints(huntPoints);
                                                                                saveNewRecord.putHuntID(questions.get(0).getHuntID());
                                                                                saveNewRecord.put("userID", ParseObject.createWithoutData("_User", user.getObjectId()));
                                                                                try {
                                                                                    saveNewRecord.save();
                                                                                } catch (ParseException e1) {
                                                                                    e1.printStackTrace();
                                                                                }
                                                                            }

                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    }
                                                });

                                            }
                                        });                                                }

                                }
                            });

                        }
                    });}
                else {
                    Log.d("ERROR", "Data not fetched");
                }
            }
        }); return null;
    }
}

