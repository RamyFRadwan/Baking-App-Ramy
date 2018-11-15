package com.ramyfradwan.bakingapp.database;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = RecipeDatabase.VERSION,
        packageName = "com.ramyfradwan.bakingapp.provider")
class RecipeDatabase {

    static final int VERSION = 1;

    @Table(IngredientColumns.class)
    static final String INGREDIENTS = "ingredients";

    @Table(RecipeColumns.class)
    static final String RECIPES = "recipes";

    @Table(StepColumns.class)
    static final String STEPS = "steps";
}