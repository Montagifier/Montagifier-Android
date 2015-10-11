package alexkang.videojerk;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Controller mController;

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

        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.actions);
        View fabAddVideo = findViewById(R.id.add_video);
        View fabSkipVideo = findViewById(R.id.skip_video);

        fabAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final EditText text = new EditText(MainActivity.this);

                builder.setMessage("Enter your YouTube URL");
                builder.setView(text);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mController.requestVideo(text.getText().toString(), new Callback() {
                            @Override
                            public void onDataRetrieved(Object data) {
                                if (data != null) {
                                    Snackbar.make(fabMenu, "Video added to queue.", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(fabMenu, "Incorrect format.", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });

                        fabMenu.collapse();
                    }
                });
                builder.setNegativeButton("Cancel", null);

                builder.show();
            }
        });

        fabSkipVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.requestSkip(new Callback() {
                    @Override
                    public void onDataRetrieved(Object data) {
                        if (data != null) {
                            Snackbar.make(fabMenu, "Skip requested.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(fabMenu, "An error occurred, please try again later.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

                fabMenu.collapse();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mController = Controller.getInstance(this);
        mController.getSounds(new Callback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataRetrieved(Object data) {
                if (data != null) {
                    mSectionsPagerAdapter.setSections((ArrayList<SoundCategory>) data);
                }
            }
        });
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<SoundCategory> sections;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            sections = new ArrayList<>();

            SoundCategory category;
            ArrayList<String> sounds;

            sounds = new ArrayList<>();
            sounds.add("Weed");
            sounds.add("Airhorn");
            sounds.add("Ayy");
            sounds.add("Lmao");
            sounds.add("Hitmarker");
            sounds.add("Intervention");
            category = new SoundCategory("Montage", sounds);
            sections.add(category);

            sounds = new ArrayList<>();
            sounds.add("Boot");
            sounds.add("Shutdown");
            sounds.add("Error");
            sounds.add("Bloop");
            sounds.add("420");
            sounds.add("VHS");
            sounds.add("Elevator");
            category = new SoundCategory("Vapor", sounds);
            sections.add(category);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
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
