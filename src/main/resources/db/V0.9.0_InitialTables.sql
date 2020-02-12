SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

CREATE TABLE public.car (
    id character varying(255) NOT NULL,
    carplate character varying(255),
    cartype character varying(255)
);


ALTER TABLE public.car OWNER TO profile_manager;

CREATE TABLE public.competence (
    id character varying(255) NOT NULL
);


ALTER TABLE public.competence OWNER TO profile_manager;

CREATE TABLE public.drivinglicense (
    id character varying(255) NOT NULL,
    drivinglicenseid character varying(255)
);


ALTER TABLE public.drivinglicense OWNER TO profile_manager;

CREATE TABLE public.material (
    id character varying(255) NOT NULL
);

ALTER TABLE public.material OWNER TO profile_manager;

CREATE TABLE public.norm (
    id character varying(255) NOT NULL,
    attribute character varying(255),
    comparison character varying(255),
    negation boolean NOT NULL,
    operator integer
);


ALTER TABLE public.norm OWNER TO profile_manager;

CREATE TABLE public.plannedactivity (
    id character varying(255) NOT NULL,
    description character varying(255),
    endtime character varying(255),
    starttime character varying(255),
    status integer
);


ALTER TABLE public.plannedactivity OWNER TO profile_manager;


CREATE TABLE public.plannedactivity_attendees (
    plannedactivity_id character varying(255) NOT NULL,
    attendees character varying(255)
);


ALTER TABLE public.plannedactivity_attendees OWNER TO profile_manager;



CREATE TABLE public.relevantlocation (
    id character varying(255) NOT NULL,
    label character varying(255),
    latitude double precision NOT NULL,
    longitude double precision NOT NULL
);


ALTER TABLE public.relevantlocation OWNER TO profile_manager;



CREATE TABLE public.routine (
    id character varying(255) NOT NULL,
    from_time character varying(255),
    label character varying(255),
    proximity character varying(255),
    to_time character varying(255)
);


ALTER TABLE public.routine OWNER TO profile_manager;



CREATE TABLE public.socialpractice (
    id character varying(255) NOT NULL,
    label character varying(255),
    competences_id character varying(255),
    materials_id character varying(255)
);


ALTER TABLE public.socialpractice OWNER TO profile_manager;



CREATE TABLE public.socialpractice_norm (
    socialpractice_id character varying(255) NOT NULL,
    norms_id character varying(255) NOT NULL
);


ALTER TABLE public.socialpractice_norm OWNER TO profile_manager;



CREATE TABLE public.wenetuserprofile (
    id character varying(255) NOT NULL,
    _creationts bigint NOT NULL,
    _lastupdatets bigint NOT NULL,
    avatar character varying(255),
    day smallint,
    month smallint,
    year integer,
    email character varying(255),
    gender integer,
    locale character varying(255),
    first character varying(255),
    last character varying(255),
    middle character varying(255),
    prefix character varying(10),
    suffix character varying(10),
    nationality character varying(255),
    occupation character varying(255),
    phonenumber character varying(255)
);


ALTER TABLE public.wenetuserprofile OWNER TO profile_manager;



CREATE TABLE public.wenetuserprofile_languages (
    wenetuserprofile_id character varying(255) NOT NULL,
    code character varying(2),
    level integer,
    name character varying(255)
);


ALTER TABLE public.wenetuserprofile_languages OWNER TO profile_manager;



CREATE TABLE public.wenetuserprofile_norm (
    wenetuserprofile_id character varying(255) NOT NULL,
    norms_id character varying(255) NOT NULL
);


ALTER TABLE public.wenetuserprofile_norm OWNER TO profile_manager;



CREATE TABLE public.wenetuserprofile_plannedactivity (
    wenetuserprofile_id character varying(255) NOT NULL,
    plannedactivities_id character varying(255) NOT NULL
);


ALTER TABLE public.wenetuserprofile_plannedactivity OWNER TO profile_manager;


CREATE TABLE public.wenetuserprofile_relationships (
    wenetuserprofile_id character varying(255) NOT NULL,
    type integer,
    userid character varying(255)
);


ALTER TABLE public.wenetuserprofile_relationships OWNER TO profile_manager;

CREATE TABLE public.wenetuserprofile_relevantlocation (
    wenetuserprofile_id character varying(255) NOT NULL,
    relevantlocations_id character varying(255) NOT NULL
);


ALTER TABLE public.wenetuserprofile_relevantlocation OWNER TO profile_manager;


CREATE TABLE public.wenetuserprofile_routine (
    wenetuserprofile_id character varying(255) NOT NULL,
    personalbehaviors_id character varying(255) NOT NULL
);


ALTER TABLE public.wenetuserprofile_routine OWNER TO profile_manager;


CREATE TABLE public.wenetuserprofile_socialpractice (
    wenetuserprofile_id character varying(255) NOT NULL,
    socialpractices_id character varying(255) NOT NULL
);


ALTER TABLE public.wenetuserprofile_socialpractice OWNER TO profile_manager;