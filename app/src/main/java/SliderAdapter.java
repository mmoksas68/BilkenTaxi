package com.example.fatihpc.bilkentransportation;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {


    Context context;
    LayoutInflater inflater;

    public SliderAdapter(Context context){

        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.i1,
            R.drawable.i2,
            R.drawable.i3,
            R.drawable.i4,
            R.drawable.i5,
            R.drawable.i6,
            R.drawable.i7,
            R.drawable.i8,
            R.drawable.i9,
            R.drawable.i10,
            R.drawable.i11,
            R.drawable.i2
    };

    public String[] slide_decs = {
            "Toggle button shows if the user is driver or passenger. The appearance of application differs according to position of this toggle. If the user chooses driver option, he/she can enter destination, otherwise destination part cannot be edited. Passengers are also able to see markers of vehicles on the map differently from drivers.",
            "Destination part is editable, only if the user is a driver. Drivers can enter their destination to be seen in the passengers’ maps.",
            "When the user clicks this button, the profile can be changed.",
            "Hamburger menu icon is accessible at every stage of application. This menu enables users to connect to other part of application.",
            "User can use the features of the application by selecting at from the hamburger menu.",
            "Passengers can see the drivers' locations and their own location on the map when \"Show Vehicles\" is clicked on hamburger menu.",
            "By clicking on the drivers' markers, user can see the destination",
            "Through the Bilkent Landmarks property, users can see the list of significant Bilkent landmarks' locations.",
            "When clicked on a particular landmarks,  location is showed on the map.",
            "In feedback part, the user can make comments on BilkenTransportation. You should enter to this part whatever subject you want to comment on before sending feedback.",
            "This part is for your detailed comments about the app.",
            "After writing comments, the user clicks “Send” button to save their feedback."
    };

    @Override
    public int getCount() {
        return slide_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){

        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView)view.findViewById(R.id.slide_image);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideDescription.setText(slide_decs[position]);

        container.addView(view);

        return view;

    }

    public void destroyItem(ViewGroup container, int position, Object object){

        container.removeView((RelativeLayout) object);
    }


}