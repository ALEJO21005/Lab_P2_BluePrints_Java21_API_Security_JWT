package co.edu.eci.blueprints.dto.response;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;

import java.util.List;

public record BlueprintResponse(
        Long id,
        String author,
        String name,
        List<Point> points,
        int pointsCount
) {
    public static BlueprintResponse fromBlueprint(Blueprint blueprint) {
        return new BlueprintResponse(
                blueprint.getId(),
                blueprint.getAuthor(),
                blueprint.getName(),
                blueprint.getPoints(),
                blueprint.getPoints() != null ? blueprint.getPoints().size() : 0
        );
    }
}
