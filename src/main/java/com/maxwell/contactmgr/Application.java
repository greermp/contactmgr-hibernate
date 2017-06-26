package com.maxwell.contactmgr;

import com.maxwell.contactmgr.model.Contact;
import com.maxwell.contactmgr.model.Contact.ContactBuilder;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by greermp on 6/24/2017.
 */
public class Application {
    /* Hold a resuable reference to a SessionFactory, since we need only one.
    * Static? - Loaded as soon as class is needed by JVM, IE, before main method
    */
    private static final SessionFactory sessionFactory = buildSessionFactory();

    // Building a Session Factory is a pain
    private static SessionFactory buildSessionFactory() {
        //First, create a standard service registry object, which will give us access to hibernates main services, including
        // JDBC connectivity, hibernate configuraiton via XML, importing intial DB data from a SQL file, and building a session factory
        /*
            First part creates a builder object that is used to create a standard service registry
            configure() loads the hibernate configuration file from its default location (hibernate.cfg.xml)
            build() makes the Standard Service Registry Object

         */

        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        // ^ First part creates a builder object that is used to create a standard service registry
        //  configure() loads the hibernate configuration file from its default location (hibernate.cfg.xml)
        // build() makes the Standard Service Registry Object




        // MetaDataSources Object- manner by which we can start loading the JPA annotated entities which we referenced in the hibernate
        // config file (contact entity)
        /*
            Build a Metadata Sources object using the registy we just built.
                Use that to build the Metadata object itself, which is the thing that encapsulates all the PRM mappings loaded from the
                annotated entities.
                Use this Metadataobject to build the session factory

                This is the object that encapsulates all the configuration of all of the mappings, as well as the configuration
                for how to connect to the database itself
         */
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();

    }

    public static void main(String[] args) {

        // We dont like this, so we use a builder pattern
        //Contact contact = new Contact (1,"Max", "Greer", "greerm@gmail.com", 7032000574L);

        //Builder pattern would look like this..
        Contact contactB = new ContactBuilder("Max", "Grer")
                .withEmail("greermp@gmail.com")
                .withPhone(7032000574L)
                .build();

        Contact contactC = new ContactBuilder("Joe", "Grer")
                .withEmail("greermp@gmail.com")
                .withPhone(7032000574L)
                .build();

        save(contactB);
        save(contactC);

        fetchAllContacts().stream().forEach(System.out::println);
        /*
        for (Contact c: fetchAllContacts()) {
            System.out.println(c);
        }
        */
        sessionFactory.close();
    }

    private static void save(Contact contact) {
        // Open a session
        Session session = sessionFactory.openSession();

        //Begin a transaction
        session.beginTransaction();

        // Use the session to save the contact
        session.save(contact);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }

    @SuppressWarnings("unchecked")
    private static List<Contact> fetchAllContacts() {
        Session session = sessionFactory.openSession();

        // UPDATED: Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // UPDATED: Create CriteriaQuery
        CriteriaQuery<Contact> criteria = builder.createQuery(Contact.class);

        // UPDATED: Specify criteria root
        criteria.from(Contact.class);

        // UPDATED: Execute query
        List<Contact> contacts = session.createQuery(criteria).getResultList();


        session.close();
        return contacts;
    }
}
