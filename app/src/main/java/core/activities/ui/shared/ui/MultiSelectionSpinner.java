package core.activities.ui.shared.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import androidx.appcompat.widget.AppCompatSpinner;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MultiSelectionSpinner extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    private OnMultipleItemsSelectedListener listener;

    String[] _items = null;
    boolean[] mSelection = null;
    boolean[] mSelectionAtStart = null;
    String _itemsAtStart = null;

    ArrayAdapter<String> arrayAdapter;

    @Getter
    @Setter
    String dialogTitle;
    @Getter
    @Setter
    String selectAllButtonText;
    @Getter
    @Setter
    String okButtonText;
    @Getter
    @Setter
    String cancelButtonText;
    @Getter
    @Setter
    boolean isEnabled;

    public interface OnMultipleItemsSelectedListener {
        void selectedIndices(List<Integer> indices, MultiSelectionSpinner spinner);

        void selectedStrings(List<String> strings, MultiSelectionSpinner spinner);
    }

    public MultiSelectionSpinner(Context context) {
        super(context);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setListener(OnMultipleItemsSelectedListener listener) {
        this.listener = listener;
    }

    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;
            arrayAdapter.clear();
            arrayAdapter.add(getSelectedItemsAsString());
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean performClick() {
        if (isEnabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(Objects.nonNull(dialogTitle) ? dialogTitle : "Select");
            builder.setMultiChoiceItems(_items, mSelection, this);
            _itemsAtStart = getSelectedItemsAsString();

            builder.setNeutralButton(Objects.nonNull(selectAllButtonText) ? selectAllButtonText : "Select All",
                    (dialog, which) -> selectAll());

            builder.setPositiveButton(Objects.nonNull(okButtonText) ? okButtonText : "Ok", (dialog, which) -> {
                System.arraycopy(mSelection, 0, mSelectionAtStart, 0, mSelection.length);
                listener.selectedIndices(getSelectedIndices(), MultiSelectionSpinner.this);
                listener.selectedStrings(getSelectedStrings(), MultiSelectionSpinner.this);
            });

            builder.setNegativeButton(Objects.nonNull(cancelButtonText) ? cancelButtonText : "Cancel", (dialog, which) -> {
                arrayAdapter.clear();
                arrayAdapter.add(_itemsAtStart);
                System.arraycopy(mSelectionAtStart, 0, mSelection, 0, mSelectionAtStart.length);
            });
            builder.show();
        }
        return true;
    }

    private void selectAll() {
        Arrays.fill(mSelection, true);
        arrayAdapter.clear();
        arrayAdapter.add(getSelectedItemsAsString());
        // trigger listener
        listener.selectedIndices(getSelectedIndices(), MultiSelectionSpinner.this);
        listener.selectedStrings(getSelectedStrings(), MultiSelectionSpinner.this);
    }

    /**
     * Only ArrayAdapter<String> acceptable
     *
     * @param adapter ArrayAdapter<String> adapter.
     */
    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter);
        arrayAdapter = (ArrayAdapter<String>) adapter;
    }

    public void setItems(String[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        mSelectionAtStart = new boolean[_items.length];
        arrayAdapter.clear();
        arrayAdapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        mSelection[0] = true;
        mSelectionAtStart[0] = true;
        // trigger listener
        listener.selectedIndices(getSelectedIndices(), MultiSelectionSpinner.this);
        listener.selectedStrings(getSelectedStrings(), MultiSelectionSpinner.this);
    }

    public void setItems(List<String> items) {
        setItems(items.toArray(new String[0]));
    }

    public void setSelection(List<String> selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                    mSelectionAtStart[j] = true;
                }
            }
        }
        arrayAdapter.clear();
        arrayAdapter.add(getSelectedItemsAsString());
        // trigger listener
        listener.selectedIndices(getSelectedIndices(), MultiSelectionSpinner.this);
        listener.selectedStrings(getSelectedStrings(), MultiSelectionSpinner.this);
    }

    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
            mSelectionAtStart[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        arrayAdapter.clear();
        arrayAdapter.add(getSelectedItemsAsString());
        // trigger listener
        listener.selectedIndices(getSelectedIndices(), MultiSelectionSpinner.this);
        listener.selectedStrings(getSelectedStrings(), MultiSelectionSpinner.this);
    }

    public void setSelection(int[] selectedIndices) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        for (int index : selectedIndices) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
                mSelectionAtStart[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        arrayAdapter.clear();
        arrayAdapter.add(getSelectedItemsAsString());
        // trigger listener
        listener.selectedIndices(getSelectedIndices(), MultiSelectionSpinner.this);
        listener.selectedStrings(getSelectedStrings(), MultiSelectionSpinner.this);
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(_items[i]);
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIndices() {
        List<Integer> selection = new LinkedList<>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    private String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }
}
