package se.fearlessgames.fear.input;

import com.google.common.collect.PeekingIterator;

public interface MouseController {
	PeekingIterator<MouseState> getEvents();
}
