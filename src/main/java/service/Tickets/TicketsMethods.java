package service.Tickets;

import model.TicketsModel;
import service.HibernateService.TicketsService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TicketsMethods {
    public static String ticketInfo(TicketsModel ticket) {
        if (ticket.getDepartureCity() == null) {
            return "Введите город отправления";
        } else if (ticket.getArrivalCity() == null) {
            return "Введите город назначения";
        } else if (ticket.getDepartureDate() == null) {
            return "Введите дату отправления";
        } else {
            return ticket.getInfoAboutTicket();
        }
    }

    public static boolean hasFullInfo(TicketsModel ticket) {
        return ticket.getDepartureCity() != null && ticket.getArrivalCity() != null && ticket.getDepartureDate() != null;
    }

    public static void addField(TicketsModel ticket,String fieldValue){
        if(ticket.getDepartureCity()==null){
            ticket.setDepartureCity(fieldValue);
        } else if(ticket.getArrivalCity()==null){
            ticket.setArrivalCity(fieldValue);
        } else if(ticket.getDepartureDate()==null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
            ticket.setDepartureDate(LocalDate.parse(fieldValue,formatter));
        }
        TicketsService.updateTicketInfo(ticket);
    }
}
