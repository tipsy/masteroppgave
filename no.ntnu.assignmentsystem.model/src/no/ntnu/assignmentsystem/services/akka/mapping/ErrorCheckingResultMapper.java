package no.ntnu.assignmentsystem.services.akka.mapping;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.assignmentsystem.editor.akka.messages.PluginErrorCheckingResult;
import no.ntnu.assignmentsystem.editor.akka.messages.PluginProblemMarker;
import no.ntnu.assignmentsystem.services.akka.messages.ErrorCheckingResult;
import no.ntnu.assignmentsystem.services.akka.messages.ProblemMarker;

public class ErrorCheckingResultMapper {
	public static ErrorCheckingResult createErrorCheckingResult(String fileId, PluginErrorCheckingResult errorCheckingResult) {
		List<ProblemMarker> problemMarkers = createProblemMarkers(errorCheckingResult.problemMarkers);
		return new ErrorCheckingResult(fileId, problemMarkers);
	}
	
	
	// --- Private methods ---
	
	private static List<ProblemMarker> createProblemMarkers(List<PluginProblemMarker> markers) {
		List<ProblemMarker> problemMarkers = new ArrayList<>();
		markers.stream().map(ErrorCheckingResultMapper::createProblemMarker).forEach(marker -> {
			problemMarkers.add(marker);
		});
		
		return problemMarkers;
	}
	
	private static ProblemMarker createProblemMarker(PluginProblemMarker marker) {
		int lineNumber = marker.lineNumber;
		ProblemMarker.Type type = createType(marker.severity);
		String description = marker.message;
		
		return new ProblemMarker(lineNumber, type, description);
	}
	
	private static ProblemMarker.Type createType(PluginProblemMarker.Severity severity) {
		switch (severity) {
		case Warning:
			return ProblemMarker.Type.Warning;
		case Info:
			return ProblemMarker.Type.Information;
		default:
			return ProblemMarker.Type.Error;
		}
	}
}
