package co.edu.eci.blueprints.services;

import co.edu.eci.blueprints.controllers.BlueprintWebSocketController;
import co.edu.eci.blueprints.dto.websocket.BlueprintWebSocketMessage;
import co.edu.eci.blueprints.filters.BlueprintsFilter;
import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;
import co.edu.eci.blueprints.persistence.BlueprintNotFoundException;
import co.edu.eci.blueprints.persistence.BlueprintPersistence;
import co.edu.eci.blueprints.persistence.BlueprintPersistenceException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BlueprintsServices {

    private final BlueprintPersistence persistence;
    private final BlueprintsFilter filter;
    private final BlueprintWebSocketController webSocketController;

    public BlueprintsServices(BlueprintPersistence persistence,
                             BlueprintsFilter filter,
                             BlueprintWebSocketController webSocketController) {
        this.persistence = persistence;
        this.filter = filter;
        this.webSocketController = webSocketController;
    }

    public void addNewBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        persistence.saveBlueprint(bp);

        // Envir notificación WebSocket
        BlueprintWebSocketMessage message = BlueprintWebSocketMessage.blueprintCreated(
                bp.getAuthor(),
                bp.getName(),
                bp.getPoints()
        );
        webSocketController.notifyGlobalBlueprintUpdate(message);
    }

    public Set<Blueprint> getAllBlueprints() {
        return persistence.getAllBlueprints().stream()
                .map(filter::apply)
                .collect(java.util.stream.Collectors.toSet());
    }

    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        return persistence.getBlueprintsByAuthor(author).stream()
                .map(filter::apply)
                .collect(java.util.stream.Collectors.toSet());
    }

    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        return filter.apply(persistence.getBlueprint(author, name));
    }

    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        persistence.addPoint(author, name, x, y);

        // Obtener el blueprint actualizado para la notificación WebSocket
        Blueprint updatedBlueprint = persistence.getBlueprint(author, name);
        Point addedPoint = new Point(x, y);

        // Enviir notificación WebSocket específica para este blueprint
        BlueprintWebSocketMessage message = BlueprintWebSocketMessage.pointAdded(
                author,
                name,
                addedPoint,
                updatedBlueprint.getPoints()
        );

        webSocketController.notifyBlueprintUpdate(author, name, message);
        webSocketController.notifyGlobalBlueprintUpdate(message);
    }

    public void deleteBlueprint(String author, String name) throws BlueprintNotFoundException {
        persistence.deleteBlueprint(author, name);

        // Enviar notificación WebSocket
        BlueprintWebSocketMessage message = BlueprintWebSocketMessage.blueprintDeleted(author, name);
        webSocketController.notifyBlueprintUpdate(author, name, message);
        webSocketController.notifyGlobalBlueprintUpdate(message);
    }

    public void deletePoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        // Crear una copia del punto antes de eliminarlo
        Point deletedPoint = new Point(x, y);

        persistence.deletePoint(author, name, x, y);

        // Obtener el blueprint actualizado para la notificación WebSocket
        Blueprint updatedBlueprint = persistence.getBlueprint(author, name);

        // Enviar notificación WebSocket específica para este blueprint
        BlueprintWebSocketMessage message = BlueprintWebSocketMessage.pointDeleted(
                author,
                name,
                deletedPoint,
                updatedBlueprint.getPoints()
        );

        webSocketController.notifyBlueprintUpdate(author, name, message);
        webSocketController.notifyGlobalBlueprintUpdate(message);
    }

    public void deleteAllBlueprints() {
        persistence.deleteAllBlueprints();

        // Enviar notificación WebSocket global
        BlueprintWebSocketMessage message = BlueprintWebSocketMessage.allBlueprintsDeleted();
        webSocketController.notifyGlobalBlueprintUpdate(message);
    }
}
