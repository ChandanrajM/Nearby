-- ────────────────────────────────────────────────────────────
-- MIGRATION: ADD OWNER_ID TO SHOPS
-- Run this in Supabase Dashboard → SQL Editor
-- ────────────────────────────────────────────────────────────

-- 1. Add the column
ALTER TABLE public.shops 
ADD COLUMN IF NOT EXISTS owner_id UUID REFERENCES public.users(id) ON DELETE SET NULL;

-- 2. (Optional) Backfill existing shops by phone matching
-- This tries to link existing shops to users with the same phone number
UPDATE public.shops s
SET owner_id = u.id
FROM public.users u
WHERE s.phone = u.phone AND s.owner_id IS NULL;

-- 3. Verify
SELECT id, name, owner_id, phone, status FROM public.shops;
