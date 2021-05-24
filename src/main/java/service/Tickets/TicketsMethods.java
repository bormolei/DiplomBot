package service.Tickets;

import model.TicketsModel;
import service.HibernateService.TicketsHibernateService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TicketsMethods {
    public static String ticketInfo(TicketsModel ticket) {
        if (ticket.getDepartureCity() == null) {
            return ticket.getInfoAboutTicket() + "\n\n" + "Введите город отправления";
        } else if (ticket.getArrivalCity() == null) {
            return ticket.getInfoAboutTicket() + "\n\n" + "Введите город назначения";
        } else if (ticket.getDepartureDate() == null) {
            return ticket.getInfoAboutTicket() + "\n\n" + "Введите дату отправления";
        }
        return ticket.getInfoAboutTicket();
    }

    public static boolean hasFullInfo(TicketsModel ticket) {
        return ticket.getDepartureCity() != null && ticket.getArrivalCity() != null && ticket.getDepartureDate() != null;
    }

    public static void addField(TicketsModel ticket, String fieldValue) {
        if (ticket.getDepartureCity() == null) {
            ticket.setDepartureCity(fieldValue);
        } else if (ticket.getArrivalCity() == null) {
            ticket.setArrivalCity(fieldValue);
        } else if (ticket.getDepartureDate() == null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
            ticket.setDepartureDate(LocalDate.parse(fieldValue, formatter));
        }
        TicketsHibernateService.updateTicketInfo(ticket);
    }
}
