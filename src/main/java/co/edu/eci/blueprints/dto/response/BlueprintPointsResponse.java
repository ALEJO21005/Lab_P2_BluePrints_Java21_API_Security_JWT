package co.edu.eci.blueprints.dto.response;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;

import java.util.List;

public record BlueprintPointsResponse(
        String author,
        String name,
        List<Point> points,
        int totalPoints,
        String message
) {
    public static BlueprintPointsResponse fromBlueprint(Blueprint blueprint, String message) {
        return new BlueprintPointsResponse(
                blueprint.getAuthor(),
                blueprint.getName(),
                blueprint.getPoints(),
                blueprint.getPoints() != null ? blueprint.getPoints().size() : 0,
                message
        );
    }

    public static BlueprintPointsResponse fromBlueprint(Blueprint blueprint) {
        return fromBlueprint(blueprint, "Point added successfully");
    }
}