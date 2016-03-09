package org.researchstack.skin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.views.IconTabLayout;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.UiThreadContext;
import org.researchstack.skin.ActionItem;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.R;
import org.researchstack.skin.TaskProvider;
import org.researchstack.skin.UiManager;
import org.researchstack.skin.notification.TaskAlertReceiver;
import org.researchstack.skin.ui.adapter.MainPagerAdapter;

import java.util.List;

import rx.Observable;

/**
 * Created by bradleymcdermott on 10/27/15.
 */
public class MainActivity extends PinCodeActivity
{
    private static final int REQUEST_CODE_INITIAL_TASK = 1010;

    private MainPagerAdapter pagerAdapter;

    //TODO Quick fix for now.
    private boolean failedToFinishInitialTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LogExt.d(getClass(), "onCreate");

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handleNotificationIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        LogExt.d(getClass(), "onNewIntent");

        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent)
    {
        LogExt.d(getClass(), "handleNotificationIntent");

        if(intent != null && intent.hasExtra(TaskAlertReceiver.KEY_NOTIFICATION_ID))
        {
            // Get the notif-id from the incoming intent
            int notificationId = intent.getIntExtra(TaskAlertReceiver.KEY_NOTIFICATION_ID, - 1);

            // Create a delete intent w/ notif-id
            Intent deleteTaskIntent = TaskAlertReceiver.createDeleteIntent(notificationId);
            sendBroadcast(deleteTaskIntent);

            // Finally, remove extra from the incoming intent so that, if activity is recreated, we
            // do not re-call this method
            intent.removeExtra(TaskAlertReceiver.KEY_NOTIFICATION_ID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE_INITIAL_TASK)
        {
            if(resultCode == RESULT_OK)
            {
                TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
                StorageAccess.getInstance().getAppDatabase().saveTaskResult(taskResult);
                DataProvider.getInstance().processInitialTaskResult(this, taskResult);
            }
            else
            {
                failedToFinishInitialTask = true;
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        for(ActionItem item : UiManager.getInstance().getMainActionBarItems())
        {
            MenuItem menuItem = menu.add(item.getGroupId(),
                    item.getId(),
                    item.getOrder(),
                    item.getTitle());
            menuItem.setIcon(item.getIcon());
            menuItem.setShowAsAction(item.getAction());
            menuItem.setIntent(new Intent(this, item.getClazz()));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        // Check if we need to run initial Task
        if(! failedToFinishInitialTask)
        {
            Observable.create(subscriber -> {
                UiThreadContext.assertBackgroundThread();

                if(! DataProvider.getInstance().isSignedIn(MainActivity.this))
                {
                    LogExt.d(getClass(), "User not signed in, skipping initial survey");
                    subscriber.onCompleted();
                    return;
                }

                TaskResult result = StorageAccess.getInstance()
                        .getAppDatabase()
                        .loadLatestTaskResult(TaskProvider.TASK_ID_INITIAL);
                subscriber.onNext(result == null);
            }).compose(ObservableUtils.applyDefault()).subscribe(needsInitialSurvey -> {
                if((boolean) needsInitialSurvey)// &&
                //                    DataProvider.getInstance().isSignedIn(MainActivity.this))
                {
                    Task task = TaskProvider.getInstance().get(TaskProvider.TASK_ID_INITIAL);
                    Intent intent = ViewTaskActivity.newIntent(this, task);
                    startActivityForResult(intent, MainActivity.REQUEST_CODE_INITIAL_TASK);
                }
            });
        }

        if(pagerAdapter == null)
        {
            List<ActionItem> items = UiManager.getInstance().getMainTabBarItems();
            pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), items);
            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(pagerAdapter);

            IconTabLayout tabLayout = (IconTabLayout) findViewById(R.id.tabLayout);
            tabLayout.setOnTabSelectedListener(new IconTabLayout.OnTabSelectedListenerAdapter()
            {
                @Override
                public void onTabSelected(TabLayout.Tab tab)
                {
                    int index = tabLayout.getSelectedTabPosition();
                    viewPager.setCurrentItem(index);
                }
            });

            for(ActionItem item : items)
            {
                tabLayout.addIconTab(
                        item.getTitle(),
                        item.getIcon(),
                        items.indexOf(item) == 0,//TODO check if should show
                        items.indexOf(item) == 0
                );
            }
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        }
    }

    @Override
    public void onDataFailed()
    {
        super.onDataFailed();
        //TODO Show dialog explaing what went wrong, instead of finishing activity.

        Toast.makeText(this, "Whoops", Toast.LENGTH_LONG).show();
        finish();
    }

}
