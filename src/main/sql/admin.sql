--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: Building; Type: TABLE; Schema: public; Owner: admin; Tablespace: 
--
DROP TABLE "Building" CASCADE;

CREATE TABLE "Building" (
    "Name" character varying(128) NOT NULL,
    "Floors" numeric NOT NULL
);


ALTER TABLE public."Building" OWNER TO admin;

--
-- Name: Floor; Type: TABLE; Schema: public; Owner: admin; Tablespace: 
--

DROP TABLE "Floor" CASCADE;

CREATE TABLE "Floor" (
    "Floor" numeric NOT NULL,
    "Building" character varying(256) NOT NULL,
    "Plan" bytea NOT NULL
);


ALTER TABLE public."Floor" OWNER TO admin;

--
-- Name: Floor_Contains; Type: TABLE; Schema: public; Owner: admin; Tablespace: 
--

DROP TABLE "Floor_Contains" CASCADE;

CREATE TABLE "Floor_Contains" (
    "Building" character varying(256) NOT NULL,
    "Floor" numeric NOT NULL,
    "Room" numeric NOT NULL
);


ALTER TABLE public."Floor_Contains" OWNER TO admin;

--
-- Name: Lift; Type: TABLE; Schema: public; Owner: admin; Tablespace: 
--

DROP TABLE "Lift" CASCADE;

CREATE TABLE "Lift" (
    "Building" character varying(256) NOT NULL,
    "ID" numeric NOT NULL
);


ALTER TABLE public."Lift" OWNER TO admin;

--
-- Name: Lift_Link; Type: TABLE; Schema: public; Owner: admin; Tablespace: 
--

DROP TABLE "Lift_Link" CASCADE;

CREATE TABLE "Lift_Link" (
    "Building" character varying(256) NOT NULL,
    "Lift_ID" numeric NOT NULL,
    "Floor" numeric NOT NULL
);


ALTER TABLE public."Lift_Link" OWNER TO admin;

--
-- Name: Room; Type: TABLE; Schema: public; Owner: admin; Tablespace: 
--

DROP TABLE "Room" CASCADE;

CREATE TABLE "Room" (
    "Number" numeric NOT NULL,
    "Building" character varying(256) NOT NULL
);


ALTER TABLE public."Room" OWNER TO admin;

--
-- Name: Staircase; Type: TABLE; Schema: public; Owner: admin; Tablespace: 
--

DROP TABLE "Staircase" CASCADE;

CREATE TABLE "Staircase" (
    "Building" character varying(256) NOT NULL,
    "ID" numeric NOT NULL
);


ALTER TABLE public."Staircase" OWNER TO admin;

--
-- Name: Stairs_Link; Type: TABLE; Schema: public; Owner: admin; Tablespace: 
--

DROP TABLE "Stairs_Link" CASCADE;

CREATE TABLE "Stairs_Link" (
    "Building" character varying(256) NOT NULL,
    "Stairs_ID" numeric NOT NULL,
    "Floor" numeric NOT NULL
);


ALTER TABLE public."Stairs_Link" OWNER TO admin;

--
-- Data for Name: Building; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY "Building" ("Name", "Floors") FROM stdin;
\.


--
-- Data for Name: Floor; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY "Floor" ("Floor", "Building", "Plan") FROM stdin;
\.


--
-- Data for Name: Floor_Contains; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY "Floor_Contains" ("Building", "Floor", "Room") FROM stdin;
\.


--
-- Data for Name: Lift; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY "Lift" ("Building", "ID") FROM stdin;
\.


--
-- Data for Name: Lift_Link; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY "Lift_Link" ("Building", "Lift_ID", "Floor") FROM stdin;
\.


--
-- Data for Name: Room; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY "Room" ("Number", "Building") FROM stdin;
\.


--
-- Data for Name: Staircase; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY "Staircase" ("Building", "ID") FROM stdin;
\.


--
-- Data for Name: Stairs_Link; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY "Stairs_Link" ("Building", "Stairs_ID", "Floor") FROM stdin;
\.


--
-- Name: Building_PK; Type: CONSTRAINT; Schema: public; Owner: admin; Tablespace: 
--

ALTER TABLE ONLY "Building"
    ADD CONSTRAINT "Building_PK" PRIMARY KEY ("Name");


--
-- Name: Floor_Contains_PK; Type: CONSTRAINT; Schema: public; Owner: admin; Tablespace: 
--

ALTER TABLE ONLY "Floor_Contains"
    ADD CONSTRAINT "Floor_Contains_PK" PRIMARY KEY ("Building", "Floor", "Room");


--
-- Name: Floor_PK; Type: CONSTRAINT; Schema: public; Owner: admin; Tablespace: 
--

ALTER TABLE ONLY "Floor"
    ADD CONSTRAINT "Floor_PK" PRIMARY KEY ("Floor", "Building");


--
-- Name: Lift_Link_PK; Type: CONSTRAINT; Schema: public; Owner: admin; Tablespace: 
--

ALTER TABLE ONLY "Lift_Link"
    ADD CONSTRAINT "Lift_Link_PK" PRIMARY KEY ("Building", "Lift_ID", "Floor");


--
-- Name: Lift_PK; Type: CONSTRAINT; Schema: public; Owner: admin; Tablespace: 
--

ALTER TABLE ONLY "Lift"
    ADD CONSTRAINT "Lift_PK" PRIMARY KEY ("Building", "ID");


--
-- Name: Room_PK; Type: CONSTRAINT; Schema: public; Owner: admin; Tablespace: 
--

ALTER TABLE ONLY "Room"
    ADD CONSTRAINT "Room_PK" PRIMARY KEY ("Number", "Building");


--
-- Name: Stair_Link_PK; Type: CONSTRAINT; Schema: public; Owner: admin; Tablespace: 
--

ALTER TABLE ONLY "Stairs_Link"
    ADD CONSTRAINT "Stair_Link_PK" PRIMARY KEY ("Building", "Stairs_ID", "Floor");


--
-- Name: Staircase_PK; Type: CONSTRAINT; Schema: public; Owner: admin; Tablespace: 
--

ALTER TABLE ONLY "Staircase"
    ADD CONSTRAINT "Staircase_PK" PRIMARY KEY ("Building", "ID");


--
-- Name: Building_Index; Type: INDEX; Schema: public; Owner: admin; Tablespace: 
--

CREATE INDEX "Building_Index" ON "Building" USING btree ("Name");


--
-- Name: Floor_Contains_Building_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Floor_Contains"
    ADD CONSTRAINT "Floor_Contains_Building_FK" FOREIGN KEY ("Building") REFERENCES "Building"("Name") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Floor_Contains_Floor_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Floor_Contains"
    ADD CONSTRAINT "Floor_Contains_Floor_FK" FOREIGN KEY ("Floor", "Building") REFERENCES "Floor"("Floor", "Building") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Floor_Contains_Room_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Floor_Contains"
    ADD CONSTRAINT "Floor_Contains_Room_FK" FOREIGN KEY ("Room", "Building") REFERENCES "Room"("Number", "Building") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Floor_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Floor"
    ADD CONSTRAINT "Floor_FK" FOREIGN KEY ("Building") REFERENCES "Building"("Name") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Lift_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Lift"
    ADD CONSTRAINT "Lift_FK" FOREIGN KEY ("Building") REFERENCES "Building"("Name") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Lift_Link_Building_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Lift_Link"
    ADD CONSTRAINT "Lift_Link_Building_FK" FOREIGN KEY ("Building") REFERENCES "Building"("Name") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Lift_Link_Floor_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Lift_Link"
    ADD CONSTRAINT "Lift_Link_Floor_FK" FOREIGN KEY ("Floor", "Building") REFERENCES "Floor"("Floor", "Building") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Lift_Link_Lift_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Lift_Link"
    ADD CONSTRAINT "Lift_Link_Lift_FK" FOREIGN KEY ("Lift_ID", "Building") REFERENCES "Lift"("ID", "Building") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Room_Building_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Room"
    ADD CONSTRAINT "Room_Building_FK" FOREIGN KEY ("Building") REFERENCES "Building"("Name") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Stair_Link_Building_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Stairs_Link"
    ADD CONSTRAINT "Stair_Link_Building_FK" FOREIGN KEY ("Building") REFERENCES "Building"("Name") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Stair_Link_Floor_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Stairs_Link"
    ADD CONSTRAINT "Stair_Link_Floor_FK" FOREIGN KEY ("Floor", "Building") REFERENCES "Floor"("Floor", "Building") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Stair_Link_Stair; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Stairs_Link"
    ADD CONSTRAINT "Stair_Link_Stair" FOREIGN KEY ("Stairs_ID", "Building") REFERENCES "Staircase"("ID", "Building") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: Staircase_FK; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY "Staircase"
    ADD CONSTRAINT "Staircase_FK" FOREIGN KEY ("Building") REFERENCES "Building"("Name") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

