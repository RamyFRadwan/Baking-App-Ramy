package com.ramyfradwan.bakingapp.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ramyfradwan.bakingapp.R;
import com.ramyfradwan.bakingapp.database.RecipeProvider;
import com.ramyfradwan.bakingapp.model.WidgetType;
import com.ramyfradwan.bakingapp.ui.RecipeWidgetProvider;
import com.ramyfradwan.bakingapp.util.PreferenceUtil;

public class RecipeWidgetService extends IntentService {

    private static final String TAG = RecipeWidgetService.class.getSimpleName();

    public static void startActionUpdateWidgets(@NonNull Context context) {
        Intent intent = new Intent(context, RecipeWidgetService.class);
        context.startService(intent);
    }

    public RecipeWidgetService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {

            WidgetType type;

            int selectedRecipeId = PreferenceUtil.getSelectedRecipeId(this);
            if (selectedRecipeId == PreferenceUtil.NO_ID) {
                // get recipes

                boolean isRecipesExist;

                try (Cursor cursor = getContentResolver().query(
                        RecipeProvider.Recipes.RECIPES,
                        null,
                        null,
                        null,
                        null
                )) {
                    isRecipesExist = cursor != null && cursor.getCount() > 0;
                } catch (Exception ex) {
                    isRecipesExist = false;
                    ex.printStackTrace();
                }

                type = isRecipesExist ? WidgetType.RECIPES : WidgetType.NONE;
            } else {
                type = WidgetType.INGREDIENTS;
            }

            // display ingredients of selected recipe id
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
            //Trigger data update to handle the GridView widgets and force a data refresh
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
            //Now update all widgets
            RecipeWidgetProvider.updateRecipeWidgets(this, appWidgetManager, appWidgetIds, type);
        }
    }
}
