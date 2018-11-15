package com.ramyfradwan.bakingapp.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ramyfradwan.bakingapp.database.IngredientColumns;
import com.ramyfradwan.bakingapp.database.RecipeColumns;
import com.ramyfradwan.bakingapp.database.RecipeProvider;
import com.ramyfradwan.bakingapp.database.StepColumns;
import com.ramyfradwan.bakingapp.model.Ingredient;
import com.ramyfradwan.bakingapp.model.Recipe;
import com.ramyfradwan.bakingapp.model.Step;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class DBUtil {

    private static final String TAG = DBUtil.class.getSimpleName();

    public static void deleteAllRecipes(@NonNull ContentResolver contentResolver) {
        contentResolver.delete(RecipeProvider.Recipes.RECIPES, null, null);
    }

    public static void deleteAllIngredients(@NonNull ContentResolver contentResolver) {
        contentResolver.delete(RecipeProvider.Ingredients.INGREDIENTS, null, null);
    }

    public static void deleteAllSteps(@NonNull ContentResolver contentResolver) {
        contentResolver.delete(RecipeProvider.Steps.STEPS, null, null);
    }

    private static ContentValues createContentValues(@NonNull Recipe recipe) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecipeColumns.RECIPE_ID, recipe.getRecipeId());
        contentValues.put(RecipeColumns.NAME, recipe.getName());
        contentValues.put(RecipeColumns.SERVINGS, recipe.getServings());
        contentValues.put(RecipeColumns.IMAGE, recipe.getImage());
        return contentValues;
    }

    private static ContentValues createContentValues(@NonNull Ingredient ingredient, int recipeId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(IngredientColumns.QUANTITY, ingredient.getQuantity());
        contentValues.put(IngredientColumns.MEASURE, ingredient.getMeasure());
        contentValues.put(IngredientColumns.NAME, ingredient.getName());
        contentValues.put(IngredientColumns.RECIPE_ID, recipeId);
        return contentValues;
    }

    private static ContentValues createContentValues(@NonNull Step step, int stepOrder, int recipeId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(StepColumns.STEP_ID, step.getStepId());
        contentValues.put(StepColumns.SHORT_DESCRIPTION, step.getShortDescription());
        contentValues.put(StepColumns.DESCRIPTION, step.getDescription());
        contentValues.put(StepColumns.VIDEO_URL, step.getVideoURL());
        contentValues.put(StepColumns.THUMBNAIL_URL, step.getThumbnailURL());
        contentValues.put(StepColumns.ORDER, stepOrder);
        contentValues.put(StepColumns.RECIPE_ID, recipeId);
        return contentValues;
    }

    public static Uri insertRecipe(@NonNull ContentResolver contentResolver, @NonNull Recipe recipe) {
        ContentValues contentValues = createContentValues(recipe);
        return contentResolver.insert(RecipeProvider.Recipes.RECIPES, contentValues);
    }

    public static void insertRecipes(@NonNull ContentResolver contentResolver, @NonNull List<Recipe> recipes) {
        ContentValues[] contentValues = new ContentValues[recipes.size()];

        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            ContentValues cv = createContentValues(recipe);
            contentValues[i] = cv;
        }

        contentResolver.bulkInsert(RecipeProvider.Recipes.RECIPES, contentValues);
    }

    @Nullable
    public static Recipe getRecipe(@NonNull ContentResolver contentResolver, int recipeId) {
        String selection = RecipeColumns.RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};

        Recipe recipe = null;

        try (Cursor cursor = contentResolver.query(
                RecipeProvider.Recipes.RECIPES,
                null,
                selection,
                selectionArgs,
                null
        )) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                int idIndex = cursor.getColumnIndex(RecipeColumns.ID);
                int recipeIdIndex = cursor.getColumnIndex(RecipeColumns.RECIPE_ID);
                int nameIndex = cursor.getColumnIndex(RecipeColumns.NAME);
                int servingIndex = cursor.getColumnIndex(RecipeColumns.SERVINGS);
                int imageIndex = cursor.getColumnIndex(RecipeColumns.IMAGE);

                recipe = new Recipe();
                recipe.setRowId(cursor.getInt(idIndex));
                recipe.setRecipeId(cursor.getInt(recipeIdIndex));
                recipe.setName(cursor.getString(nameIndex));
                recipe.setServings(cursor.getInt(servingIndex));
                recipe.setImage(cursor.getString(imageIndex));
            }
        } catch (Exception ex) {
            Timber.e(ex);
            ex.printStackTrace();
        }

        if (recipe != null) {
            // get its ingredients
            List<Ingredient> ingredients = getIngredients(contentResolver, recipeId);
            recipe.setIngredients(ingredients);

            // get its steps
            List<Step> steps = getSteps(contentResolver, recipeId);
            recipe.setSteps(steps);
        }

        return recipe;
    }

    public static List<Recipe> getRecipes(@NonNull ContentResolver contentResolver) {
        String[] projection = {RecipeColumns.RECIPE_ID};

        List<Integer> recipeIds = null;

        try (Cursor cursor = contentResolver.query(
                RecipeProvider.Recipes.RECIPES,
                projection,
                null,
                null,
                null
        )) {
            if (cursor != null && cursor.getCount() > 0) {
                recipeIds = new ArrayList<>();

                int recipeIdIndex = cursor.getColumnIndex(RecipeColumns.RECIPE_ID);

                while (cursor.moveToNext()) {
                    int id = cursor.getInt(recipeIdIndex);
                    recipeIds.add(id);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        List<Recipe> recipes = null;

        if (recipeIds != null && !recipeIds.isEmpty()) {

            recipes = new ArrayList<>();

            for (int id : recipeIds) {
                Recipe recipe = getRecipe(contentResolver, id);
                recipes.add(recipe);
            }
        }

        return recipes;
    }

    public static void insertIngredients(@NonNull ContentResolver contentResolver, @NonNull List<Ingredient> ingredients, int recipeId) {
        ContentValues[] contentValues = new ContentValues[ingredients.size()];

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ContentValues cv = createContentValues(ingredient, recipeId);
            contentValues[i] = cv;
        }

        contentResolver.bulkInsert(RecipeProvider.Ingredients.INGREDIENTS, contentValues);
    }

    @Nullable
    private static List<Ingredient> getIngredients(@NonNull ContentResolver contentResolver, int recipeId) {
        String selection = IngredientColumns.RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};

        List<Ingredient> ingredients = null;

        try (Cursor cursor = contentResolver.query(RecipeProvider.Ingredients.INGREDIENTS, null, selection, selectionArgs, null)) {
            if (cursor != null && cursor.getCount() > 0) {

                int quantityIndex = cursor.getColumnIndex(IngredientColumns.QUANTITY);
                int measureIndex = cursor.getColumnIndex(IngredientColumns.MEASURE);
                int nameIndex = cursor.getColumnIndex(IngredientColumns.NAME);

                ingredients = new ArrayList<>();

                while (cursor.moveToNext()) {
                    Ingredient ingredient = new Ingredient();

                    ingredient.setQuantity(cursor.getDouble(quantityIndex));
                    ingredient.setMeasure(cursor.getString(measureIndex));
                    ingredient.setName(cursor.getString(nameIndex));

                    ingredients.add(ingredient);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ingredients;
    }

    public static void insertSteps(@NonNull ContentResolver contentResolver, @NonNull List<Step> steps, int recipeId) {
        ContentValues[] contentValues = new ContentValues[steps.size()];

        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            ContentValues cv = createContentValues(step, i, recipeId);
            contentValues[i] = cv;
        }

        contentResolver.bulkInsert(RecipeProvider.Steps.STEPS, contentValues);
    }

    @Nullable
    private static List<Step> getSteps(@NonNull ContentResolver contentResolver, int recipeId) {
        String selection = StepColumns.RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        String sortOrder = StepColumns.ORDER;

        List<Step> steps = null;

        try (Cursor cursor = contentResolver.query(RecipeProvider.Steps.STEPS, null, selection, selectionArgs, sortOrder)) {
            if (cursor != null && cursor.getCount() > 0) {
                int idIndex = cursor.getColumnIndex(StepColumns.ID);
                int stepIdIndex = cursor.getColumnIndex(StepColumns.STEP_ID);
                int shortDescriptionIndex = cursor.getColumnIndex(StepColumns.SHORT_DESCRIPTION);
                int descriptionIndex = cursor.getColumnIndex(StepColumns.DESCRIPTION);
                int videoUrlIndex = cursor.getColumnIndex(StepColumns.VIDEO_URL);
                int thumbnailUrlIndex = cursor.getColumnIndex(StepColumns.THUMBNAIL_URL);

                steps = new ArrayList<>();

                while (cursor.moveToNext()) {
                    Step step = new Step();

                    step.setRowId(cursor.getInt(idIndex));
                    step.setStepId(cursor.getInt(stepIdIndex));
                    step.setShortDescription(cursor.getString(shortDescriptionIndex));
                    step.setDescription(cursor.getString(descriptionIndex));
                    step.setVideoURL(cursor.getString(videoUrlIndex));
                    step.setThumbnailURL(cursor.getString(thumbnailUrlIndex));

                    steps.add(step);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return steps;
    }
}
