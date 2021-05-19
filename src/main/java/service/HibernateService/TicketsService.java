package service.HibernateService;

import DAO.HibernateController;
import model.TicketsModel;
import model.User;
import utils.Actions;

public class TicketsService {
    static TicketsModel localTicket = new TicketsModel();

    public static void addNewTicket(TicketsModel ticketsModel) {
        HibernateController.doHibernateAction(ticketsModel, Actions.SAVE);
    }

    public static void updateTicketInfo(TicketsModel ticketsModel) {
        HibernateController.doHibernateAction(ticketsModel, Actions.UPDATE);
    }

    public static TicketsModel getTicketInfo(User user) {
        return (TicketsModel) HibernateController.getRowsByField(localTicket, "chatId", user.getId()).get(0);
    }

    public static void deleteTicket(TicketsModel ticketsModel) {
        HibernateController.doHibernateAction(ticketsModel, Actions.DELETE);
    }
}
