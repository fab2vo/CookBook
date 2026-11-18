package com.fdx.cookbook;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * "Planifier" dialog: puts a recipe in a meal of the family or personal agenda.
 * Opened by a long press on the photo in the recipe list, or from the menu of the recipe screen.
 * Defaults: today, lunch before 14:00 and dinner after, family agenda.
 */
public class AgendaPlanDialog {

    private static final LocalTime LUNCH_LIMIT = LocalTime.of(14, 0);

    private final Context mContext;
    private final Recipe mRecipe;
    private LocalDate mDate;

    private AgendaPlanDialog(Context c, Recipe r) {
        mContext = c;
        mRecipe = r;
        mDate = LocalDate.now();
    }

    public static void show(Context c, Recipe r) {
        new AgendaPlanDialog(c, r).build();
    }

    private void build() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_agenda_plan, null);
        Button dateButton = v.findViewById(R.id.plan_date_button);
        RadioGroup mealGroup = v.findViewById(R.id.plan_meal_group);
        RadioGroup scopeGroup = v.findViewById(R.id.plan_scope_group);

        dateButton.setText(mDate.format(AgendaEditDialog.DAY_LONG));
        dateButton.setOnClickListener(b -> new DatePickerDialog(mContext,
                (picker, y, m, d) -> {
                    mDate = LocalDate.of(y, m + 1, d);
                    dateButton.setText(mDate.format(AgendaEditDialog.DAY_LONG));
                },
                mDate.getYear(), mDate.getMonthValue() - 1, mDate.getDayOfMonth()).show());
        mealGroup.check(LocalTime.now().isBefore(LUNCH_LIMIT) ? R.id.plan_meal_lunch : R.id.plan_meal_dinner);
        scopeGroup.check(R.id.plan_scope_family);

        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.PAG_plan_title, mRecipe.getTitle()))
                .setView(v)
                .setPositiveButton(android.R.string.ok, null)   // set below, to keep the dialog open on error
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> {
            Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            ok.setOnClickListener(b -> {
                AgendaEntry.Meal meal = mealGroup.getCheckedRadioButtonId() == R.id.plan_meal_lunch
                        ? AgendaEntry.Meal.LUNCH : AgendaEntry.Meal.DINNER;
                AgendaEntry.Scope scope = scopeGroup.getCheckedRadioButtonId() == R.id.plan_scope_family
                        ? AgendaEntry.Scope.FAMILY : AgendaEntry.Scope.PERSO;
                AgendaEntry e = new AgendaEntry(scope, mDate, meal);
                e.setRecipe(mRecipe.getId(), mRecipe.getTitle());
                ok.setEnabled(false);
                new AgendaApi(mContext).add(e, (status, unused) -> {
                    ok.setEnabled(true);
                    if (status == AgendaApi.Status.OK) {
                        dialog.dismiss();
                        String msg = mContext.getString(R.string.PAG_planned, mRecipe.getTitle(),
                                AgendaEditDialog.mealName(mContext, meal),
                                mDate.format(AgendaEditDialog.DAY_LONG),
                                mContext.getString(scope == AgendaEntry.Scope.FAMILY
                                        ? R.string.PAG_tab_family : R.string.PAG_tab_perso));
                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, AgendaEditDialog.statusMessage(status), Toast.LENGTH_LONG).show();
                    }
                });
            });
        });
        dialog.show();
    }
}
