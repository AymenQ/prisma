package com.puzzletimer.models;

import static com.puzzletimer.Internationalization._;

import java.util.HashMap;
import java.util.UUID;

public class Category {
    private final UUID categoryId;
    private final String scramblerId;
    private final String description;
    private final boolean isUserDefined;
    private final String[] tipIds;

    public Category(UUID categoryId, String scramblerId, String description, boolean isUserDefined, String[] tipIds) {
        this.categoryId = categoryId;
        this.scramblerId = scramblerId;
        this.description = description;
        this.isUserDefined = isUserDefined;
        this.tipIds = tipIds;
    }

    public UUID getCategoryId() {
        return this.categoryId;
    }

    public String getScramblerId() {
        return this.scramblerId;
    }

    public Category setScramblerId(String scramblerId) {
        return new Category(
            this.categoryId,
            scramblerId,
            this.description,
            this.isUserDefined,
            this.tipIds);
    }

    public String getDescription() {
        // descriptions of built-in categories
        HashMap<UUID, String> descriptions = new HashMap<UUID, String>();
        descriptions.put(UUID.fromString("64b9c16d-dc36-44b4-9605-c93933cdd311"), _("category.2x2x2_cube"));
        descriptions.put(UUID.fromString("90dea358-e525-4b6c-8b2d-abfa61f02a9d"), _("category.rubiks_cube"));
        descriptions.put(UUID.fromString("3282c6bc-3a7b-4b16-aeae-45ae75b17e47"), _("category.rubiks_cube_one_handed"));
        descriptions.put(UUID.fromString("953a7701-6235-4f9b-8dd4-fe32055cb652"), _("category.rubiks_cube_blindfolded"));
        descriptions.put(UUID.fromString("761088a1-64fc-47db-92ea-b6c3b812e6f3"), _("category.rubiks_cube_with_feet"));
        descriptions.put(UUID.fromString("3577f24a-065b-4bcc-9ca3-3df011d07a5d"), _("category.4x4x4_cube"));
        descriptions.put(UUID.fromString("587d884a-b996-4cd6-95bb-c3dafbfae193"), _("category.4x4x4_cube_blindfolded"));
        descriptions.put(UUID.fromString("e3894e40-fb85-497b-a592-c81703901a95"), _("category.5x5x5_cube"));
        descriptions.put(UUID.fromString("0701c98c-a275-4e51-888c-59dc9de9de1a"), _("category.5x5x5_cube_blindfolded"));
        descriptions.put(UUID.fromString("86227762-6249-4417-840b-3c8ba7b0bd33"), _("category.6x6x6_cube"));
        descriptions.put(UUID.fromString("b9375ece-5a31-4dc4-b58e-ecb8a638e102"), _("category.7x7x7_cube"));
        descriptions.put(UUID.fromString("08831818-6d8c-41fb-859e-a29b507f49fa"), _("category.8x8x8_cube"));
        descriptions.put(UUID.fromString("2fe5cacf-55df-4f5c-b811-f64c54959c44"), _("category.9x9x9_cube"));
        descriptions.put(UUID.fromString("7f244648-0e14-44cd-8399-b41ccdb6d7db"), _("category.rubiks_clock"));
        descriptions.put(UUID.fromString("c50f60c8-99d2-48f4-8502-d110a0ef2fc9"), _("category.megaminx"));
        descriptions.put(UUID.fromString("6750cbfd-542d-42b7-9cf4-56265549dd88"), _("category.pyraminx"));
        descriptions.put(UUID.fromString("748e6c09-cca5-412a-bd92-cc7febed9adf"), _("category.square_1"));
        descriptions.put(UUID.fromString("1a647910-41ff-48d1-b9f5-6f1874da9265"), _("category.rubiks_magic"));
        descriptions.put(UUID.fromString("f8f96514-bcb8-4f46-abb5-aecb7da4e4de"), _("category.master_magic"));

        if (descriptions.containsKey(this.categoryId)) {
            return descriptions.get(this.categoryId);
        }

        return this.description;
    }

    public Category setDescription(String description) {
        return new Category(
            this.categoryId,
            this.scramblerId,
            description,
            this.isUserDefined,
            this.tipIds);
    }

    public boolean isUserDefined() {
        return this.isUserDefined;
    }

    public String[] getTipIds() {
        return this.tipIds;
    }

    public Category setTipIds(String[] tipIds) {
        return new Category(
            this.categoryId,
            this.scramblerId,
            this.description,
            this.isUserDefined,
            tipIds);
    }
}
