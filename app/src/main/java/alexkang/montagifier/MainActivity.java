package alexkang.montagifier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Controller mController;
    private FloatingActionsMenu mFabMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);

        mFabMenu = (FloatingActionsMenu) findViewById(R.id.actions);
        View fabAddVideo = findViewById(R.id.add_video);
        View fabSkipVideo = findViewById(R.id.skip_video);

        fabAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoDialog(null);
            }
        });

        fabSkipVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.requestSkip(new Callback() {
                    @Override
                    public void onDataRetrieved(Object data) {
                        if (data != null) {
                            Snackbar.make(mFabMenu, "Skip requested.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(mFabMenu, "An error occurred, please try again later.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

                mFabMenu.collapse();
            }
        });

        if (getIntent().getAction().equals(Intent.ACTION_SEND)) {
            openVideoDialog(getIntent().getStringExtra(Intent.EXTRA_TEXT));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mController = Controller.getInstance();
        mController.getSounds(new Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataRetrieved(Object data) {
                if (data != null) {
                    ArrayList<SoundCategory> categories = (ArrayList<SoundCategory>) data;
                    Collections.sort(categories, new Comparator<SoundCategory>() {
                        @Override
                        public int compare(SoundCategory lhs, SoundCategory rhs) {
                            return lhs.name.compareTo(rhs.name);
                        }
                    });
                    mSectionsPagerAdapter.setSections(categories);
                }
            }
        });
        mController.checkIn();
    }

    @Override
    public void onPause() {
        super.onPause();

        mController.checkOut();
    }

    private void openVideoDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText text = new EditText(MainActivity.this);

        if (url != null) {
            text.setText(url);
        }

        builder.setMessage("Enter your YouTube URL");
        builder.setView(text);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mController.requestVideo(text.getText().toString(), new Callback() {
                    @Override
                    public void onDataRetrieved(Object data) {
                        if (data != null) {
                            Snackbar.make(mFabMenu, "Video added to queue.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(mFabMenu, "An error occurred, please try again later.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

                mFabMenu.collapse();
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<SoundCategory> sections;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            sections = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("category", sections.get(position).name);
            bundle.putSerializable("sounds", sections.get(position).sounds);

            BoardFragment fragment = new BoardFragment();
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return sections.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sections.get(position).name;
        }

        public void setSections(ArrayList<SoundCategory> sections) {
            this.sections = sections;
            notifyDataSetChanged();
        }
    }

}
