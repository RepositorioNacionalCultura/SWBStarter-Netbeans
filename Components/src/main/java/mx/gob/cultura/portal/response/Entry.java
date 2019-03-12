/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.response;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
/**
 *
 * @author sergio.tellez
 */
public class Entry implements Serializable {

    private static final long serialVersionUID = 7680915584896844702L;

    private String _id;
    private List<String> holder;
    private List<String> description;
    
    private String lugar;
    private String bictype;
    private Stats resourcestats;
    private List<String> creator;
    private Period periodcreated;
    private List<Title> recordtitle;
    private DateDocument datecreated;
    private List<String> resourcetype;
    private List<Identifier> identifier;
    private List<DigitalObject> digitalObject;
    
    private String type;
    private Integer position;
    
    private List<String> lang;
    private List<String> keywords;
    private List<String> collection;
    
    private Rights rights;
    private List<String> generator;
    private String resourcethumbnail;
    
    private String state;
    private String reference;
    private String holdernote;
    private String dimension;
    private String unidad;
    private String chapter;
    private String episode;
    private String availableformats;
    private String documentalfund;
    private String direction;
    private String production;
    private String music;
    private String libreto;
    private String musicaldirection;
    private String number;
    private String subtile;
    private String editorial;
    private String press;
    private String director;
    private String producer;
    private String screenplay;
    private String distribution;
    private String tipo_de_identificador;
    private String tipo_de_unidad;
    private String tipo_de_dimension;
    private String period;
    private String techmaterial;
    private String hiperonimo;
    private String acervo;
    private String incripcion;
    private String inscripcionobra;
    private String origin; 
    private String cultura;
    private String curaduria;
    private String discipline;
    private String collectionnote;
    private String material;
    private String gprlang;
    private String genre;
    private String movs;
    private String acts;
    private String clase;
    private String observations;
    private String biccustodyentity;
    private String theme;
    private String synopsis;
    private String invited;
    private String publisher;
    private String characters;
    private String category;
    private String subcategory;
    private String clasification;
    private String culturalregion;
    private String technique;
    private String catalog;
    private String culturaoaiid;
    private String url_mediateca;
    
    private List<String> serie;
    private List<String> credits;
    private List<String> creatorgroup;
    private List<String> reccollection;

    public String getCulturaoaiid() {
        return culturaoaiid;
    }

    public void setCulturaoaiid(String culturaoaiid) {
        this.culturaoaiid = culturaoaiid;
    }

    public String getBictype() {
        return bictype;
    }

    public void setBictype(String bictype) {
        this.bictype = bictype;
    }
    
    public Stats getResourcestats() {
        return resourcestats;
    }

    public void setResourcestats(Stats resourcestats) {
        this.resourcestats = resourcestats;
    }

    public List<Title> getRecordtitle() {
        return recordtitle;
    }

    public void setRecordtitle(List<Title> recordtitle) {
        this.recordtitle = recordtitle;
    }

    public List<String> getResourcetype() {
        return resourcetype;
    }

    public void setResourcetype(List<String> resourcetype) {
        this.resourcetype = resourcetype;
    }
    
    public Rights getRights() {
        return rights;
    }

    public void setRights(Rights rights) {
        this.rights = rights;
    }
    
    public Entry() {
        init();
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public List<String> getCreator() {
        return creator;
    }

    public void setCreator(List<String> creator) {
        this.creator = creator;
    }

    public Period getPeriodcreated() {
        return periodcreated;
    }

    public void setPeriodcreated(Period periodcreated) {
        this.periodcreated = periodcreated;
    }

    public DateDocument getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(DateDocument datecreated) {
        this.datecreated = datecreated;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<DigitalObject> getDigitalObject() {
        return digitalObject;
    }

    public void setDigitalObject(List<DigitalObject> digitalObject) {
        this.digitalObject = digitalObject;
    }

    public List<String> getLang() {
        return lang;
    }

    public void setLang(List<String> lang) {
        this.lang = lang;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getCollection() {
        return collection;
    }

    public void setCollection(List<String> collection) {
        this.collection = collection;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResourcethumbnail() {
        return resourcethumbnail;
    }

    public void setResourcethumbnail(String resourcethumbnail) {
        this.resourcethumbnail = resourcethumbnail;
    }

    public List<String> getHolder() {
        return holder;
    }

    public void setHolder(List<String> holder) {
        this.holder = holder;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public List<String> getGenerator() {
        return generator;
    }

    public void setGenerator(List<String> generator) {
        this.generator = generator;
    }
    
    private void init() {
        DateDocument date = new DateDocument();
        date.setValue("");
        this.type = "";
        this.datecreated = date;
        periodcreated = new Period();
        periodcreated.setDateend(date);
        periodcreated.setDatestart(date);
        this.resourcestats = new Stats();
        this.keywords = new ArrayList<>();
        this.description = new ArrayList<>();
        this.recordtitle = new ArrayList<>();
        this.rights = new Rights();
        this.rights.setMedia(new MediaType());
        this.rights.getMedia().setMime("");
        this._id = "";
        this.digitalObject = new ArrayList<>();
    }
    
    public String getIdentifiers() {
        StringBuilder identifiers = new StringBuilder();
        if (null != this.identifier) {
            for (Identifier ide : this.identifier) {
		identifiers.append(ide.getValue()).append(" |");
            }
            if (identifiers.length() > 0)
                identifiers.delete(identifiers.length()-2, identifiers.length());
	}
        return identifiers.toString();
    }
    
    public String getResourcetypes() {
        StringBuilder builder = new StringBuilder();
        for (String t : this.getResourcetype()) {
            builder.append(t).append(", ");
        }
	if (builder.length() > 0) builder.deleteCharAt(builder.length() - 2);
        return builder.toString();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getHoldernote() {
        return holdernote;
    }

    public void setHoldernote(String holdernote) {
        this.holdernote = holdernote;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public String getAvailableformats() {
        return availableformats;
    }

    public void setAvailableformats(String availableformats) {
        this.availableformats = availableformats;
    }

    public String getDocumentalfund() {
        return documentalfund;
    }

    public void setDocumentalfund(String documentalfund) {
        this.documentalfund = documentalfund;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getLibreto() {
        return libreto;
    }

    public void setLibreto(String libreto) {
        this.libreto = libreto;
    }

    public String getMusicaldirection() {
        return musicaldirection;
    }

    public void setMusicaldirection(String musicaldirection) {
        this.musicaldirection = musicaldirection;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSubtile() {
        return subtile;
    }

    public void setSubtile(String subtile) {
        this.subtile = subtile;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getScreenplay() {
        return screenplay;
    }

    public void setScreenplay(String screenplay) {
        this.screenplay = screenplay;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getTipo_de_identificador() {
        return tipo_de_identificador;
    }

    public void setTipo_de_identificador(String tipo_de_identificador) {
        this.tipo_de_identificador = tipo_de_identificador;
    }

    public String getTipo_de_unidad() {
        return tipo_de_unidad;
    }

    public void setTipo_de_unidad(String tipo_de_unidad) {
        this.tipo_de_unidad = tipo_de_unidad;
    }

    public String getTipo_de_dimension() {
        return tipo_de_dimension;
    }

    public void setTipo_de_dimension(String tipo_de_dimension) {
        this.tipo_de_dimension = tipo_de_dimension;
    }

    public List<String> getCredits() {
        return credits;
    }

    public void setCredits(List<String> credits) {
        this.credits = credits;
    }

    public List<String> getSerie() {
        return serie;
    }

    public void setSerie(List<String> serie) {
        this.serie = serie;
    }

    public List<String> getReccollection() {
        return reccollection;
    }

    public void setReccollection(List<String> reccollection) {
        this.reccollection = reccollection;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getTechmaterial() {
        return techmaterial;
    }

    public void setTechmaterial(String techmaterial) {
        this.techmaterial = techmaterial;
    }

    public String getHiperonimo() {
        return hiperonimo;
    }

    public void setHiperonimo(String hiperonimo) {
        this.hiperonimo = hiperonimo;
    }

    public String getAcervo() {
        return acervo;
    }

    public void setAcervo(String acervo) {
        this.acervo = acervo;
    }

    public String getIncripcion() {
        return incripcion;
    }

    public void setIncripcion(String incripcion) {
        this.incripcion = incripcion;
    }

    public String getInscripcionobra() {
        return inscripcionobra;
    }

    public void setInscripcionobra(String inscripcionobra) {
        this.inscripcionobra = inscripcionobra;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCultura() {
        return cultura;
    }

    public void setCultura(String cultura) {
        this.cultura = cultura;
    }

    public String getCuraduria() {
        return curaduria;
    }

    public void setCuraduria(String curaduria) {
        this.curaduria = curaduria;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getCollectionnote() {
        return collectionnote;
    }

    public void setCollectionnote(String collectionnote) {
        this.collectionnote = collectionnote;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public List<String> getCreatorgroup() {
        return creatorgroup;
    }

    public void setCreatorgroup(List<String> creatorgroup) {
        this.creatorgroup = creatorgroup;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getInvited() {
        return invited;
    }

    public void setInvited(String invited) {
        this.invited = invited;
    }

    public String getCulturalregion() {
        return culturalregion;
    }

    public void setCulturalregion(String culturalregion) {
        this.culturalregion = culturalregion;
    }

    public String getCharacters() {
        return characters;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getClasification() {
        return clasification;
    }

    public void setClasification(String clasification) {
        this.clasification = clasification;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getGprlang() {
        return gprlang;
    }

    public void setGprlang(String gprlang) {
        this.gprlang = gprlang;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getMovs() {
        return movs;
    }

    public void setMovs(String movs) {
        this.movs = movs;
    }

    public String getActs() {
        return acts;
    }

    public void setActs(String acts) {
        this.acts = acts;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getBiccustodyentity() {
        return biccustodyentity;
    }

    public void setBiccustodyentity(String biccustodyentity) {
        this.biccustodyentity = biccustodyentity;
    }

    public String getTechnique() {
        return technique;
    }

    public void setTechnique(String technique) {
        this.technique = technique;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getUrl_mediateca() {
        return url_mediateca;
    }

    public void setUrl_mediateca(String url_mediateca) {
        this.url_mediateca = url_mediateca;
    }
    
    @Override
    public String toString() {
        return "Entry{" + "_id=" + _id + ", holder=" + holder + ", resourcetype=" + resourcetype + ", digitalObject=" + digitalObject + ", rights=" + rights + ", resourcethumbnail=" + resourcethumbnail + ", serie=" + serie + '}';
    }
}