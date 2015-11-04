package com.jhueventtrackr;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class EventListTabFrag extends ListFragment {
	private int index, numEvents;
	private String[] eventNames = null;
	private String[] eventDesc = null;
	private String[] eventStart = null;
	private String[] eventEnd = null;
	private EventListArrayAdapter adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = getArguments();
		index = data.getInt("idx");

		if (index == 0) {
			updateLists((ArrayList<Event>) FilterActivity
					.getFilteredEventList(getActivity().getApplicationContext()));
		}
		if (index == 1) {
			updateLists(EventCollection.watching);
		}
		if (index == 2) {
			Log.d("wednesday", index + "");
			updateLists(EventCollection.created);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Context context = getActivity().getApplicationContext();
		// if the event size has changed and if there is a filter in place,
		// update the displayed event list
		if ((index == 0 && numEvents != EventCollection.events.size())
				|| !FilterActivity.getFilterStrings(context).isEmpty()) {
			updateLists((ArrayList<Event>) FilterActivity
					.getFilteredEventList(context));
		}
		if (index == 1 && numEvents != EventCollection.watching.size()) {
			updateLists(EventCollection.watching);
		}
		if (index == 2 && numEvents != EventCollection.created.size()) {
			Log.d("wednesday", index + ", on resume");
			updateLists(EventCollection.created);
		}
	}

	public void updateLists(ArrayList<Event> eventList) {
		int eventCount = 0;
		numEvents = eventList.size();
		eventNames = new String[numEvents];
		eventDesc = new String[numEvents];
		eventStart = new String[numEvents];
		eventEnd = new String[numEvents];
		if (numEvents > 0) // Avoid array index out of bounds error
			for (Event event : eventList) {
				eventNames[eventCount] = event.name;
				eventDesc[eventCount] = event.desc;
				eventStart[eventCount] = event.readableStartTime()
						+ event.readableEndTime();
				eventEnd[eventCount] = event.readableEndTime();
				eventCount++;
			}
		adapter = new EventListArrayAdapter(this.getActivity(), eventNames,
				eventStart, eventDesc);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.eventlistfragment, null);
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Event e = null;
		Context context = getActivity().getApplicationContext();
		switch (index) {
		case (0):
			e = FilterActivity.getFilteredEventList(context).get((int) id);
			break;
		case (1):
			e = EventCollection.watching.get((int) id);
			break;
		case (2):
			e = EventCollection.created.get((int) id);
			break;
		}
		displayEventDialogue(getActivity(), e, (int) id);
	}

	private void displayEventDialogue(final Activity activity,
			final Event event, final int eventIndex) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(event.name);
		builder.setMessage("When: " + event.readableStartTime()
				+ event.readableEndTime() + "\nWhere: " + event.getLocation()
				+ "\n\nDescription:\n" + event.getDescWithName());
		if (index == 0) {
			builder.setPositiveButton("Add to\nTRACKING",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (!EventCollection.addWatchingEvent(event)) {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										getActivity());
								builder.setTitle("Error!");
								builder.setMessage("You are already tracking this event!");
								builder.setPositiveButton("Okay",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
											}
										});
								AlertDialog dialog2 = builder.create();
								dialog2.show();
							}
						}
					});
		} else if (index == 1) {
			builder.setPositiveButton("Remove from\nTRACKING",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							EventCollection.deleteWatchingEvent(event);
							updateLists(EventCollection.watching);
						}
					});
		} else if (index == 2) {
			builder.setPositiveButton("Edit Event",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							((EventListActivity) getActivity())
									.editEvent(event);
						}
					});
			builder.setNeutralButton("Delete Event",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							builder.setTitle("Confirm");
							builder.setMessage("Do you really want to delete your event:\n"
									+ event.getName());
							builder.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											Log.d("gothere", "before remove from remote");
											RemoteDB.removeEvent(event);
											Log.d("gothere", "after remove from remote");
											EventCollection.deleteCreatedEvent(event);
											Log.d("gothere", "after remove from local");
											updateLists(EventCollection.created);
											MapActivity.showToastMessage("Event Deleted!", getActivity());
											// adapter = new EventListArrayAdapter(activity,
											// eventNames, eventStart, eventDesc);
											// setListAdapter(adapter);
										}
									});
							builder.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
										}
									});
							AlertDialog dialog2 = builder.create();
							dialog2.show();
						}
					});
		}
		builder.setNegativeButton("Back",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

}
