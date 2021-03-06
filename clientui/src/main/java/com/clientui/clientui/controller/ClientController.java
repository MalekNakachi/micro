package com.clientui.clientui.controller;


import com.clientui.beans.CommandeBean;
import com.clientui.clientui.com.clientui.clientui.beans.PaiementBean;
import com.clientui.clientui.com.clientui.clientui.beans.ProductBean;
import com.clientui.clientui.com.clientui.clientui.proxies.MicroserviceCommandeProxy;
import com.clientui.clientui.com.clientui.clientui.proxies.MicroservicePaiementProxy;
import com.clientui.clientui.com.clientui.clientui.proxies.MicroserviceProduitsProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class ClientController {
    private final MicroserviceProduitsProxy produitsProxy;
    @Autowired
    private MicroserviceCommandeProxy CommandesProxy;
    @Autowired
    private MicroservicePaiementProxy paiementProxy;
    public ClientController(MicroserviceProduitsProxy produitsProxy){
        this.produitsProxy = produitsProxy;
    }

    @RequestMapping("/produits")
    public String accueil(Model model){
        List<ProductBean> produits =  produitsProxy.listeDesProduits();
        model.addAttribute("produits", produits);
        return "Accueil";
    }
    @RequestMapping("/details-produit/{id}")

    public String ficheProduit(@PathVariable int id, Model model){
        ProductBean produit = produitsProxy.recupererUnProduit(id);
        model.addAttribute("produit", produit);
        return "FicheProduit";
    }
    @RequestMapping(value = "/commander-produit/{idProduit}/{montant}")
    public String passerCommande(@PathVariable int idProduit, @PathVariable Double montant,  Model model){


        CommandeBean commande = new CommandeBean();

        //On renseigne les propri??t??s de l'objet de type CommandeBean que nous avons cr??e
        commande.setProductId(idProduit);
        commande.setQuantite(1);
        commande.setDateCommande(new Date());

        //appel du microservice commandes gr??ce ?? Feign et on r??cup??re en retour les d??tails de la commande cr????e, notamment son ID (??tape 4).
        CommandeBean commandeAjoutee = CommandesProxy.ajouterCommande(commande);

        //on passe ?? la vue l'objet commande et le montant de celle-ci afin d'avoir les informations n??cessaire pour le paiement
        model.addAttribute("commande", commandeAjoutee);
        model.addAttribute("montant", montant);

        return "Paiement";
    }

    /*
     * ??tape (5)
     * Op??ration qui fait appel au microservice de paiement pour traiter un paiement
     * */
    @RequestMapping(value = "/payer-commande/{idCommande}/{montantCommande}")
    public String payerCommande(@PathVariable int idCommande, @PathVariable Double montantCommande, Model model){

        PaiementBean paiementAExcecuter = new PaiementBean();

        //on reseigne les d??tails du produit
        paiementAExcecuter.setIdCommande(idCommande);
        paiementAExcecuter.setMontant(montantCommande);
        paiementAExcecuter.setNumeroCarte(numcarte()); // on g??n??re un num??ro au hasard pour simuler une CB

        // On appel le microservice et (??tape 7) on r??cup??re le r??sultat qui est sous forme ResponseEntity<PaiementBean> ce qui va nous permettre de v??rifier le code retour.
        ResponseEntity<PaiementBean> paiement = paiementProxy.payerUneCommande(paiementAExcecuter);

        Boolean paiementAccepte = false;
        //si le code est autre que 201 CREATED, c'est que le paiement n'a pas pu aboutir.
        if(paiement.getStatusCode() == HttpStatus.CREATED)
            paiementAccepte = true;

        model.addAttribute("paiementOk", paiementAccepte); // on envoi un Boolean paiementOk ?? la vue

        return "confirmation";
    }

    //G??n??re une serie de 16 chiffres au hasard pour simuler vaguement une CB
    private Long numcarte() {

        return ThreadLocalRandom.current().nextLong(1000000000000000L,9000000000000000L );
    }
}
