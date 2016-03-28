package com.chenkewen.administrator.myhelper.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chenkewen.administrator.myhelper.R;
import com.chenkewen.administrator.myhelper.data.Const;
import com.chenkewen.administrator.myhelper.model.IAlertDialogButtonListener;
import com.chenkewen.administrator.myhelper.model.IWordButtonClickListener;
import com.chenkewen.administrator.myhelper.model.Song;
import com.chenkewen.administrator.myhelper.model.WordButton;
import com.chenkewen.administrator.myhelper.myui.MyGridView;
import com.chenkewen.administrator.myhelper.util.MyLog;
import com.chenkewen.administrator.myhelper.util.MyPlayer;
import com.chenkewen.administrator.myhelper.util.Util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IWordButtonClickListener {

    /**答案状态 -- 正确 */
    public static final int STATUS_ANSWER_RIGHT = 1;
    /**答案状态 -- 错误 */
    public static final int STATUS_ANSWER_WRONG = 2;
    /**答案状态 -- 不完整 */
    public static final int STATUS_ANSWER_LACK = 3;
    //闪烁次数
    public static final int SPASH_TIMES = 6;

    public static final String TAG = "MainActivity";

    public static final int ID_DIALOG_DELETE_WORD = 1;

    public static final int ID_DIALOG_TIP_ANSWER = 2;

    public static final int ID_DIALOG_LACK_COINS = 3;


    //唱片动画
    private Animation mPanAnim;
    //LinearInterpolator动画重开始到结束是属于线性的播放模式
    private LinearInterpolator mPanLin;

    private Animation mBarInAnim;
    private LinearInterpolator mBarInLin;

    private Animation mBarOutAnim;
    private LinearInterpolator mBarOutLin;

    //过关界面
    private View mPassView;

    //播放按键
    private ImageButton mBtnPlayStart;
    //盘片和播杆
    private ImageView mViewPan;
    private ImageView mViewPanBar;
    //是否是播放状态
    private boolean mIsRunning = false;

    //定义一个文字框容器
    private ArrayList<WordButton> mAllWords;

    private ArrayList<WordButton> mBtnSelectWords;

    private MyGridView mMyGridView;

    //定义已选文字框UI容器
    private LinearLayout mViewWordsContainer;

    //当前的歌曲
    private Song mCurrentSong;

    //当前关卡的索引
    private int mCurrentStageIndex = -1;

    //当前关的索引
    private TextView mCurrentStagePassView;

    private TextView mCurrentStageView;

    //当前关卡歌曲的名称
    private TextView mCurrentSongNamePassView;

    //当前金币的数量
    private int mCurrentCoins = Const.TOTAL_COINS;

    //金币对应的View
    private TextView mViewCurrentCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //读取数据
        int[] datas = Util.loadData(this);
        mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
        mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];

        //初始化动画
        initAnimation();

        //初始化控件
        initView();

        //初始化事件
        initEvent();

        //初始化游戏数据
        initCurrentStageData();

        //处理删除按键事件
        handleDeleteWord();

        //处理提示按键事件
        handleTipAnswer();


    }

    private void initEvent() {
        mBtnPlayStart.setOnClickListener(this);

        mBarInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //启动播杆动画结束，开始唱片动画
                mViewPan.startAnimation(mPanAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mPanAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //唱片动画结束启动结束播杆动画
                mViewPanBar.startAnimation(mBarOutAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mBarOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //整套动画播放完毕
                mIsRunning = false;
                mBtnPlayStart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //注册监听器
        mMyGridView.setOnWordButtonClickListener(this);
    }

    private void initView() {
        mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
        mViewPan = (ImageView) findViewById(R.id.image_view1);
        mViewPanBar = (ImageView) findViewById(R.id.image_view2);
        mMyGridView = (MyGridView) findViewById(R.id.gridview);
        mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
        mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
        mViewCurrentCoins.setText(mCurrentCoins + "");

    }

    private void initAnimation() {
        //获取动画
        mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mPanLin = new LinearInterpolator();
        //动画播放完毕停留在原地
        mPanAnim.setFillAfter(true);
        //动画和模式关联
        mPanAnim.setInterpolator(mPanLin);

        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setFillAfter(true);
        mBarInAnim.setInterpolator(mBarInLin);

        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setFillAfter(true);
        mBarOutAnim.setInterpolator(mBarOutLin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_start:
                handlePlayButton();

                break;
        }
    }

    /**
     * 处理圆盘中间播放按钮，开始播放音乐动画
     */
    public void handlePlayButton() {
        if (mViewPanBar != null) {
            if (!mIsRunning) {
                mIsRunning = true;
                //开始播杆进入动画
                mViewPanBar.startAnimation(mBarInAnim);
                mBtnPlayStart.setVisibility(View.INVISIBLE);

                //播放音乐
                MyPlayer.playSong(MainActivity.this, mCurrentSong.getSongFileName());
            }
        }
    }

    @Override
    protected void onPause() {
        //保存游戏数据
        Util.saveData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);

        //暂停动画
        mViewPan.clearAnimation();

        //暂停音乐
        MyPlayer.stopTheSong(MainActivity.this);


        super.onPause();


    }

    private Song loadStageSongInfo(int stageIndex) {
        Song song = new Song();

        String[] stage = Const.SONG_INFO[stageIndex];
        song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
        song.setSongName(stage[Const.INDEX_SONG_NAME]);
        return song;
    }

    /**
     * 加载当前关的数据
     */
    private void initCurrentStageData() {
        //读取当前关的歌曲信息
        mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
        //初始化已选择框
        mBtnSelectWords = initWordSelect();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90, 90);

        //清空原来的答案
        mViewWordsContainer.removeAllViews();

        //增加新的答案框
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton, params);
        }

        //显示当前关的索引
        mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
        if (mCurrentStageView != null) {
            mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
        }

        //获得数据
        mAllWords = initAllWord();

        //更新数据 MyGridView
        mMyGridView.updateData(mAllWords);

        //一开始播放音乐
        handlePlayButton();

    }

    /**
     * 初始化待选文字框
     *
     * @return
     */
    private ArrayList<WordButton> initAllWord() {

        ArrayList<WordButton> data = new ArrayList<WordButton>();

        //获得所有待选文字
        String[] words = generateWords();

        for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
            WordButton button = new WordButton();
            button.mWoldStirng = words[i];
            data.add(button);
        }

        return data;
    }

    /**
     * 初始化已选文字框
     */
    private ArrayList<WordButton> initWordSelect() {
        ArrayList<WordButton> data = new ArrayList<>();

        for (int i = 0; i < mCurrentSong.getNameLenght(); i++) {
            View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);

            final WordButton holder = new WordButton();

            holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
            holder.mViewButton.setTextColor(Color.WHITE);
            holder.mViewButton.setText("");
            holder.mIsVisiable = false;

            holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
            holder.mViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearTheAnswer(holder);
                }
            });
            data.add(holder);
        }

        return data;
    }

    @Override
    public void onWordButtonClick(WordButton wordButton) {
        //  Toast.makeText(MainActivity.this, wordButton.mIndex + "", Toast.LENGTH_SHORT).show();
        setSelectWord(wordButton);

        //获得答案状态
        int checkResult = checkTheAnswer();

        if (checkResult == STATUS_ANSWER_RIGHT) {
            //如果答案正确，过关并奖励
            handlePassEvent();
        } else if (checkResult == STATUS_ANSWER_WRONG) {
            //如果答案错误，选择框文字变红并闪烁3次
            sparkTheWords();
        } else if (checkResult == STATUS_ANSWER_LACK) {
            //如果答案不完整，继续等待选择
            //答案缺失情况下文字为白色
            for (int i = 0; i < mBtnSelectWords.size(); i++) {
                mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
            }
        }
    }

    /**
     * 处理过关事件
     */
    private void handlePassEvent() {
        //显示过关界面
        mPassView = (LinearLayout) this.findViewById(R.id.pass_view);
        mPassView.setVisibility(View.VISIBLE);

        //停止未完成的动画
        mViewPan.clearAnimation();

        //停止正播放的音乐
        MyPlayer.stopTheSong(MainActivity.this);

        //播放过关的音效
        MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_SONG_COIN);

        //当前关的索引
        mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
        if (mCurrentStagePassView != null) {
            mCurrentStagePassView.setText(mCurrentStageIndex + 1 + "");
        }

        //当前关歌曲的名称
        mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_pass);
        if (mCurrentSongNamePassView != null) {
            mCurrentSongNamePassView.setText(mCurrentSong.getmSongName());
        }

        mCurrentCoins += Const.PASS_STAGE_COINS;
        mViewCurrentCoins.setText(mCurrentCoins + "");

        //下一关按键处理
        ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (judegAppPass()) {
                    //进入到通关界面
                    Util.startActivity(MainActivity.this, AllPassView.class);
                } else {
                    //开始新的一关
                    mPassView.setVisibility(View.GONE);

                    //加载关卡数据
                    initCurrentStageData();
                }
            }
        });
    }

    /**
     * 判断是否通关
     * @return
     */
    private boolean judegAppPass() {
        return (mCurrentStageIndex == Const.SONG_INFO.length - 1);
    }


    //清除答案
    private void clearTheAnswer(WordButton wordButton) {
        //设置已选框可见性
        wordButton.mViewButton.setText("");
        wordButton.mWoldStirng = "";
        wordButton.mIsVisiable = false;

        //设置待选框可见性
        setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);

    }

    //设置答案
    private void setSelectWord(WordButton wordButton) {
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            if (mBtnSelectWords.get(i).mWoldStirng.length() == 0) {
                //设置答案框内文字可见性
                mBtnSelectWords.get(i).mViewButton.setText(wordButton.mWoldStirng);
                mBtnSelectWords.get(i).mIsVisiable = true;
                mBtnSelectWords.get(i).mWoldStirng = wordButton.mWoldStirng;

                //设置记录索引
                mBtnSelectWords.get(i).mIndex = wordButton.mIndex;

                //设置Log........
                MyLog.i(TAG, mBtnSelectWords.get(i).mIndex + "");
                //设置待选框可见性
                setButtonVisiable(wordButton, View.INVISIBLE);

                break;
            }
        }
    }

    /**
     * 设置待选文字框是否可见
     * @param button
     * @param visibility
     */
    private void setButtonVisiable(WordButton button, int visibility) {
        button.mViewButton.setVisibility(visibility);
        button.mIsVisiable = (visibility == View.VISIBLE) ? true : false;

        MyLog.i(TAG, button.mIsVisiable + "");
        //Log......
    }

    /**
     * 生成所有的待选文字
     */
    private String[] generateWords() {
        Random random = new Random();

        String[] words = new String[MyGridView.COUNTS_WORDS];
        //存入歌名
        for (int i = 0; i < mCurrentSong.getNameLenght(); i++) {
            words[i] = mCurrentSong.getNameCharacters()[i] + "";
        }
        //获取随机文字并存入数组
        for (int i = mCurrentSong.getNameLenght(); i < MyGridView.COUNTS_WORDS; i++) {
            words[i] = getRandomChar() + "";
        }
        //打乱文字顺序,随机选取一个文字与最后一个文字交换，再随机依次与倒数第二个交换，依次如此
        for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--) {
            int index = random.nextInt(i + 1);

            String buf = words[index];
            words[index] = words[i];
            words[i] = buf;

        }

        return words;
    }

    /**
     * 生成随机汉字
     * @return
     */
    private char getRandomChar() {
        String str = "";
        int hightPos;
        int lowPos;

        Random random = new Random();

        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.charAt(0);

    }

    /**
     * 检查答案
     */
    private int checkTheAnswer() {
        //检查长度
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            if (mBtnSelectWords.get(i).mWoldStirng.length() == 0) {
                return STATUS_ANSWER_LACK;
            }
        }

        //检查是否正确
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            buf.append(mBtnSelectWords.get(i).mWoldStirng);
        }

        return (buf.toString().equals(mCurrentSong.getmSongName())) ?
                STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;

    }

    /**
     * 文字闪烁
     */
    private void sparkTheWords() {
        //定时器相关
        TimerTask task = new TimerTask() {
            boolean mChange = false;
            int mSpardTimes = 0;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (++mSpardTimes > SPASH_TIMES) {
                            return;
                        }

                        //执行闪烁逻辑：交替显示红色和白色文字
                        for (int i = 0; i < mBtnSelectWords.size(); i++) {
                            mBtnSelectWords.get(i).mViewButton.setTextColor(
                                    mChange ? Color.RED : Color.WHITE
                            );
                        }

                        mChange = !mChange;
                    }
                });
            }
        };
        //加载任务
        Timer timer = new Timer();
        timer.schedule(task, 1, 150);
    }

    /**
     * 自动选择一个答案
     */
    private void tipAnswer() {
        boolean tipWord = false;
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            if (mBtnSelectWords.get(i).mWoldStirng.length() == 0) {
                //根据当前答案框填入对应的文字
                onWordButtonClick(findIsAnswerWord(i));

                tipWord = true;
                //减少金币数量
                if (!handleCoins(-getTipWordCoins())) {
                    //金币数量不够，显示提示对话框
                    showConfirmDialog(ID_DIALOG_LACK_COINS);
                    return;
                }
                break;
            }
        }

        //没有找到可以填充的答案
        if (!tipWord) {
            //闪烁文字提示用户
            sparkTheWords();
        }

    }

    /**
     * 删除文字
     */
    private void deleteOnWord() {
        //减少金币
        if (!handleCoins(-getDeleteWordCoins())) {
            //金币不够，显示提示对话框
            showConfirmDialog(ID_DIALOG_LACK_COINS);
            return;
        }

        //将这个对应的WordButton设置为不可见
        setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
    }

    /**
     * 找到一个不是答案的文件，并且当前是可见的
     * @return
     */
    private WordButton findNotAnswerWord() {
        Random random = new Random();

        WordButton buf = null;

        while (true) {
            int index = random.nextInt(MyGridView.COUNTS_WORDS);

            buf = mAllWords.get(index);

            if (buf.mIsVisiable && !isTheAnswerWord(buf)) {
                return buf;
            }
        }
    }

    /**
     * 找到一个答案文字
     * @param index 当前需要填入答案框的索引
     * @return
     */
    private WordButton findIsAnswerWord(int index) {
        WordButton buf = null;

        for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {

            buf = mAllWords.get(i);

            if (buf.mWoldStirng.equals("" + mCurrentSong.getNameCharacters()[index])) {
                return buf;
            }
        }

        return null;
    }

    /**
     * 判断某个文字是否为答案
     * @param word
     * @return
     */
    private boolean isTheAnswerWord(WordButton word) {
        boolean result = false;

        for (int i = 0; i < mCurrentSong.getNameLenght(); i++) {
            if (word.mWoldStirng.equals("" + mCurrentSong.getNameCharacters()[i])) {
                result = true;

                break;
            }
        }

        return result;
    }

    /**
     * 增加或减少指定数量的金币
     * @param data
     * @return true 增加或减少成功   false 失败
     */
    private boolean handleCoins(int data) {
        //判断当前金币数量是否可被减少
        if (mCurrentCoins + data >= 0) {
            mCurrentCoins += data;

            mViewCurrentCoins.setText(mCurrentCoins + "");

            return true;
        } else {
            //金币不够
            return false;
        }
    }

    /**
     * 从配置文件里读取删除操作所有的金币
     * @return
     */
    private int getDeleteWordCoins() {
        return this.getResources().getInteger(R.integer.pay_delete_word);
    }

    /**
     * 从配置文件里读取提示操作所有的金币
     * @return
     */
    private int getTipWordCoins() {
        return this.getResources().getInteger(R.integer.pay_tip_answer);
    }
    //删除一个错误答案
    private void handleDeleteWord() {
        ImageButton button = (ImageButton) findViewById(R.id.btn_delete_word);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                deleteOnWord();
                showConfirmDialog(ID_DIALOG_DELETE_WORD);
            }
        });
    }
    //提示一个正确答案
    private  void handleTipAnswer() {
        ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tipAnswer();
                showConfirmDialog(ID_DIALOG_TIP_ANSWER);
            }
        });
    }

    //自定义AlertDialog响应事件

    //删除错误答案
    private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener() {
        @Override
        public void onClick() {
            //执行事件
            deleteOnWord();
        }
    };

    //答案提示
    private IAlertDialogButtonListener mBtnOkTipAnswerListener = new IAlertDialogButtonListener() {
        @Override
        public void onClick() {
            //执行事件
            tipAnswer();
        }
    };

    //金币不足
    private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener() {
        @Override
        public void onClick() {
            //执行事件
        }
    };

    /**
     * 显示对话框
     * @param id
     */
    private void showConfirmDialog(int id) {
        switch (id) {
            case ID_DIALOG_DELETE_WORD:
                Util.showDialog(MainActivity.this, "确认花掉" + getDeleteWordCoins() +
                        "个金币去掉一个错误答案？", mBtnOkDeleteWordListener);
                break;
            case ID_DIALOG_TIP_ANSWER:
                Util.showDialog(MainActivity.this, "确认花掉" + getTipWordCoins() +
                        "个金币获得一个文字提示？", mBtnOkTipAnswerListener);
                break;
            case ID_DIALOG_LACK_COINS:
                Util.showDialog(MainActivity.this, "金币不足，去商店补充！", mBtnOkLackCoinsListener);
                break;
        }
    }







}





























