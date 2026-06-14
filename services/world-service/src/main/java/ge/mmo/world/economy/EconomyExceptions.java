package ge.mmo.world.economy;

/** Domain failures of the economy module. */
public final class EconomyExceptions {
    private EconomyExceptions() {
    }
}

class ItemNotFoundException extends RuntimeException {
    ItemNotFoundException(String code) {
        super("Item not found: " + code);
    }
}

class ListingNotFoundException extends RuntimeException {
    ListingNotFoundException() {
        super("Listing not found");
    }
}

class InsufficientItemsException extends RuntimeException {
    InsufficientItemsException() {
        super("Not enough of that item");
    }
}

class InsufficientGoldException extends RuntimeException {
    InsufficientGoldException() {
        super("Not enough gold");
    }
}

class ListingNotActiveException extends RuntimeException {
    ListingNotActiveException() {
        super("Listing is no longer active");
    }
}

/** You cannot buy your own listing, or cancel someone else's. */
class ListingOwnershipException extends RuntimeException {
    ListingOwnershipException(String detail) {
        super(detail);
    }
}

class NoGatherableItemsException extends RuntimeException {
    NoGatherableItemsException() {
        super("Nothing to gather here");
    }
}
