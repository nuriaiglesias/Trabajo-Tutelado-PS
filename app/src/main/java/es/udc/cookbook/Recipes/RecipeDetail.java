package es.udc.cookbook.Recipes;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import es.udc.cookbook.R;

public class RecipeDetail extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail);

        TextView titleDt = findViewById(R.id.TitleDetail);
        TextView ingredientsDt = findViewById(R.id.IngredientsDetail);
        TextView instructionsDt = findViewById(R.id.InstructionsDetail);
        ImageView imageDt = findViewById(R.id.ImageDetail);

        String title = getIntent().getStringExtra("title");
        String ingredients = getIntent().getStringExtra("ingredients");
        for (int i = 0; i < ingredients.length(); i++) {
            char c = ingredients.charAt(i);
            System.out.print(i + c + " ");
        }
        String instructions = getIntent().getStringExtra("instructions");
        titleDt.setText(title);
        ingredientsDt.setText(ingredients);
        instructionsDt.setText(instructions);
    }
}